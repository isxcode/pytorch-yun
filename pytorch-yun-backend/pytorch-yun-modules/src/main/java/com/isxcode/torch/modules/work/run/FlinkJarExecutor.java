package com.isxcode.torch.modules.work.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.agent.constants.AgentApi;
import com.isxcode.torch.api.agent.pojos.req.GetJobInfoReq;
import com.isxcode.torch.api.agent.pojos.req.GetJobLogReq;
import com.isxcode.torch.api.agent.pojos.req.SubmitJobReq;
import com.isxcode.torch.api.agent.pojos.res.GetJobInfoRes;
import com.isxcode.torch.api.agent.pojos.res.GetJobLogRes;
import com.isxcode.torch.api.agent.pojos.res.SubmitJobRes;
import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.pojos.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.api.work.constants.WorkLog;
import com.isxcode.torch.api.work.constants.WorkType;
import com.isxcode.torch.api.work.exceptions.WorkRunException;
import com.isxcode.torch.api.work.pojos.dto.JarJobConfig;
import com.isxcode.torch.api.work.pojos.res.RunWorkRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.backend.api.base.pojos.BaseResponse;
import com.isxcode.torch.backend.api.base.properties.IsxAppProperties;
import com.isxcode.torch.common.locker.Locker;
import com.isxcode.torch.common.utils.AesUtils;
import com.isxcode.torch.common.utils.http.HttpUrlUtils;
import com.isxcode.torch.common.utils.http.HttpUtils;
import com.isxcode.torch.common.utils.path.PathUtils;
import com.isxcode.torch.modules.cluster.entity.ClusterEntity;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.torch.modules.cluster.repository.ClusterRepository;
import com.isxcode.torch.modules.file.entity.FileEntity;
import com.isxcode.torch.modules.file.repository.FileRepository;
import com.isxcode.torch.modules.file.service.FileService;
import com.isxcode.torch.modules.work.entity.WorkConfigEntity;
import com.isxcode.torch.modules.work.entity.WorkEntity;
import com.isxcode.torch.modules.work.entity.WorkInstanceEntity;
import com.isxcode.torch.modules.work.repository.WorkConfigRepository;
import com.isxcode.torch.modules.work.repository.WorkInstanceRepository;
import com.isxcode.torch.modules.work.repository.WorkRepository;
import com.isxcode.torch.modules.workflow.repository.WorkflowInstanceRepository;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.isxcode.torch.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.torch.common.utils.ssh.SshUtils.scpJar;


@Service
@Slf4j
public class FlinkJarExecutor extends WorkExecutor {

    private final WorkInstanceRepository workInstanceRepository;

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final WorkRepository workRepository;

    private final WorkConfigRepository workConfigRepository;

    private final IsxAppProperties isxAppProperties;

    private final Locker locker;

    private final HttpUrlUtils httpUrlUtils;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final FileService fileService;

    private final FileRepository fileRepository;

    public FlinkJarExecutor(WorkInstanceRepository workInstanceRepository, ClusterRepository clusterRepository,
        ClusterNodeRepository clusterNodeRepository, WorkflowInstanceRepository workflowInstanceRepository,
        WorkRepository workRepository, WorkConfigRepository workConfigRepository, IsxAppProperties isxAppProperties,
        Locker locker, HttpUrlUtils httpUrlUtils, ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils,
        FileService fileService, FileRepository fileRepository) {

        super(workInstanceRepository, workflowInstanceRepository);
        this.workInstanceRepository = workInstanceRepository;
        this.clusterRepository = clusterRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.workRepository = workRepository;
        this.workConfigRepository = workConfigRepository;
        this.isxAppProperties = isxAppProperties;
        this.locker = locker;
        this.httpUrlUtils = httpUrlUtils;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.fileService = fileService;
        this.fileRepository = fileRepository;
    }

    @Override
    protected void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

        // 获取日志构造器
        StringBuilder logBuilder = workRunContext.getLogBuilder();

        // 检测计算集群是否存在
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始申请资源 \n");
        if (Strings.isEmpty(workRunContext.getClusterConfig().getClusterId())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎未配置 \n");
        }

        // 检查计算集群是否存在
        Optional<ClusterEntity> calculateEngineEntityOptional =
            clusterRepository.findById(workRunContext.getClusterConfig().getClusterId());
        if (!calculateEngineEntityOptional.isPresent()) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 计算引擎不存在 \n");
        }

        // 检测集群中是否有合法节点
        List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
            .findAllByClusterIdAndStatus(calculateEngineEntityOptional.get().getId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群\n");
        }

        // 节点选择随机数
        ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("申请资源完成，激活节点:【")
            .append(engineNode.getName()).append("】\n");
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成 \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 开始构建作业
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始构建作业 \n");

        // 获取作业jar与lib文件信息并scp至所选节点
        JarJobConfig jarJobConfig = workRunContext.getJarJobConfig();
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始传输jar与其依赖 \n");

        // 获取jar
        Optional<FileEntity> fileEntityOptional = fileRepository.findById(jarJobConfig.getJarFileId());
        if (!fileEntityOptional.isPresent()) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Jar文件异常 : 请检查文件是否存在 \n");
        }
        FileEntity jarFile = fileEntityOptional.get();

        // 上传文件到制定节点路径
        // /file/id.jar
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));
        String fileDir = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
            + File.separator + TENANT_ID.get();
        try {
            scpJar(scpFileEngineNodeDto, fileDir + File.separator + jarFile.getId(),
                engineNode.getAgentHomePath() + File.separator + "zhihuiyun-agent" + File.separator + "file"
                    + File.separator + jarFile.getId() + ".jar");
        } catch (JSchException | SftpException | InterruptedException | IOException e) {
            log.debug(e.getMessage());
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "jar文件上传失败\n");
        }

        // 上传依赖到制定节点路径
        if (workRunContext.getLibConfig() != null) {
            List<FileEntity> libFile = fileRepository.findAllById(workRunContext.getLibConfig());
            libFile.forEach(e -> {
                try {
                    scpJar(scpFileEngineNodeDto, fileDir + File.separator + e.getId(),
                        engineNode.getAgentHomePath() + File.separator + "zhihuiyun-agent" + File.separator + "file"
                            + File.separator + e.getId() + ".jar");
                } catch (JSchException | SftpException | InterruptedException | IOException ex) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "jar文件上传失败\n");
                }
            });
        }

        // 开始构造SparkSubmit
        SubmitJobReq submitJobReq = SubmitJobReq.builder().entryClass(jarJobConfig.getMainClass())
            .appName(jarJobConfig.getAppName()).appResource(jarFile.getId() + ".jar")
            .agentHomePath(engineNode.getAgentHomePath()).workType(WorkType.FLINK_JAR)
            .workInstanceId(workInstance.getId()).programArgs(Arrays.asList(jarJobConfig.getArgs()))
            .agentType(calculateEngineEntityOptional.get().getClusterType()).build();

        // 构建作业完成，并打印作业配置信息
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("构建作业完成 \n");
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始提交作业 \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 加锁，必须等待作业提交成功后才能中止
        Integer lock = locker.lock("REQUEST_" + workInstance.getId());
        SubmitJobRes submitJobRes;
        try {
            submitJobRes = new RestTemplate().postForObject(
                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentApi.submitJob),
                submitJobReq, SubmitJobRes.class);
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("提交作业成功 : ")
                .append(submitJobRes.getJobId()).append("\n");
            workInstance.setSparkStarRes(JSON.toJSONString(submitJobRes));
            workInstance = updateInstance(workInstance, logBuilder);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "提交作业失败 : " + e.getMessage() + "\n");
        } finally {
            locker.unlock(lock);
        }

        // 提交作业成功后，开始循环判断状态
        while (true) {

            // 获取作业状态并保存
            GetJobInfoReq jobInfoReq = GetJobInfoReq.builder().agentHome(engineNode.getAgentHomePath())
                .jobId(submitJobRes.getJobId()).agentType(calculateEngineEntityOptional.get().getClusterType()).build();
            GetJobInfoRes getJobInfoRes;
            try {
                getJobInfoRes = HttpUtils.doPost(
                    httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentApi.getJobInfo),
                    jobInfoReq, GetJobInfoRes.class);
                log.debug("获取远程获取状态日志:{}", getJobInfoRes);
            } catch (Exception e) {
                throw new WorkRunException(
                    LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业状态异常 : " + e.getMessage() + "\n");
            }

            // 解析返回状态，并保存
            workInstance.setSparkStarRes(JSON.toJSONString(getJobInfoRes));
            logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("运行状态:")
                .append(getJobInfoRes.getStatus()).append("\n");
            workInstance = updateInstance(workInstance, logBuilder);

            // 如果状态是运行中，更新日志，继续执行
            List<String> runningStatus = Arrays.asList("RUNNING", "UNDEFINED", "SUBMITTED", "CONTAINERCREATING");
            if (runningStatus.contains(getJobInfoRes.getStatus().toUpperCase())) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "睡眠线程异常 : " + e.getMessage() + "\n");
                }
            } else {
                // 运行结束逻辑

                // 如果是中止，直接退出
                if ("KILLED".equalsIgnoreCase(getJobInfoRes.getStatus())
                    || "TERMINATING".equalsIgnoreCase(getJobInfoRes.getStatus())) {
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "作业运行中止" + "\n");
                }

                // 获取日志并保存
                GetJobLogReq getJobLogReq = GetJobLogReq.builder().agentHomePath(engineNode.getAgentHomePath())
                    .jobId(submitJobRes.getJobId()).workInstanceId(workInstance.getId())
                    .agentType(calculateEngineEntityOptional.get().getClusterType()).build();
                GetJobLogRes getJobLogRes;
                try {
                    getJobLogRes = HttpUtils.doPost(
                        httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentApi.getJobLog),
                        getJobLogReq, GetJobLogRes.class);
                } catch (IOException e) {
                    throw new WorkRunException(
                        LocalDateTime.now() + WorkLog.ERROR_INFO + "获取作业日志异常 : " + e.getMessage() + "\n");
                }

                logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("日志保存成功 \n");
                if (getJobLogRes != null) {
                    workInstance.setTaskManagerLog(getJobLogRes.getLog());
                }
                updateInstance(workInstance, logBuilder);

                // 如果运行成功，则保存返回数据
                List<String> successStatus = Arrays.asList("FINISHED", "SUCCEEDED", "COMPLETED");
                if (successStatus.contains(getJobInfoRes.getStatus().toUpperCase())) {
                    // 没有数据保存
                } else {
                    // 任务运行错误
                    throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "任务运行异常" + "\n");
                }

                // 运行结束，则退出死循环
                break;
            }
        }
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        // 判断作业有没有提交成功
        locker.lock("REQUEST_" + workInstance.getId());
        try {
            workInstance = workInstanceRepository.findById(workInstance.getId()).get();
            if (!Strings.isEmpty(workInstance.getSparkStarRes())) {
                RunWorkRes wokRunWorkRes = JSON.parseObject(workInstance.getSparkStarRes(), RunWorkRes.class);
                if (!Strings.isEmpty(wokRunWorkRes.getAppId())) {
                    // 关闭远程线程
                    WorkEntity work = workRepository.findById(workInstance.getWorkId()).get();
                    WorkConfigEntity workConfig = workConfigRepository.findById(work.getConfigId()).get();
                    List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
                        .findAllByClusterIdAndStatus(workConfig.getClusterConfig(), ClusterNodeStatus.RUNNING);
                    if (allEngineNodes.isEmpty()) {
                        throw new WorkRunException(
                            LocalDateTime.now() + WorkLog.ERROR_INFO + "申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
                    }
                    ClusterEntity cluster = clusterRepository.findById(workConfig.getClusterConfig()).get();

                    // 节点选择随机数
                    ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("appId", wokRunWorkRes.getAppId());
                    paramsMap.put("agentType", cluster.getClusterType());
                    paramsMap.put("sparkHomePath", engineNode.getFlinkHomePath());
                    BaseResponse<?> baseResponse = HttpUtils.doGet(
                        httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), "/yag/stopJob"),
                        paramsMap, null, BaseResponse.class);

                    if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                        throw new IsxAppException(baseResponse.getCode(), baseResponse.getMsg(), baseResponse.getErr());
                    }
                } else {
                    // 先杀死进程
                    WORK_THREAD.get(workInstance.getId()).interrupt();
                }
            }
        } finally {
            locker.clearLock("REQUEST_" + workInstance.getId());
        }
    }

    /**
     * 初始化spark作业提交配置.
     */
    public Map<String, String> genSparkSubmitConfig(Map<String, String> sparkConfig) {

        // 过滤掉，前缀不包含spark.xxx的配置，spark
        // submit中必须都是spark.xxx
        Map<String, String> sparkSubmitConfig = new HashMap<>();
        sparkConfig.forEach((k, v) -> {
            if (k.startsWith("spark")) {
                sparkSubmitConfig.put(k, v);
            }
        });
        return sparkSubmitConfig;
    }
}
