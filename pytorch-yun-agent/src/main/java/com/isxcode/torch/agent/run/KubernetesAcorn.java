package com.isxcode.torch.agent.run;

import com.alibaba.fastjson2.JSON;
import com.isxcode.torch.api.agent.pojos.req.GetJobInfoReq;
import com.isxcode.torch.api.agent.pojos.req.GetJobLogReq;
import com.isxcode.torch.api.agent.pojos.req.StopJobReq;
import com.isxcode.torch.api.agent.pojos.req.SubmitJobReq;
import com.isxcode.torch.api.agent.pojos.res.GetJobInfoRes;
import com.isxcode.torch.api.agent.pojos.res.GetJobLogRes;
import com.isxcode.torch.api.agent.pojos.res.StopJobRes;
import com.isxcode.torch.api.agent.pojos.res.SubmitJobRes;
import com.isxcode.torch.api.api.constants.PathConstants;
import com.isxcode.torch.api.work.constants.WorkType;
import com.isxcode.torch.backend.api.base.exceptions.AgentResponseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.kubernetes.KubernetesClusterClientFactory;
import org.apache.flink.kubernetes.KubernetesClusterDescriptor;
import org.apache.flink.kubernetes.configuration.KubernetesConfigOptions;
import org.apache.flink.kubernetes.configuration.KubernetesDeploymentTarget;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class KubernetesAcorn implements AcornRun {

    @Override
    public SubmitJobRes submitJob(SubmitJobReq submitJobReq) {

        submitJobReq.setFlinkHome(submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME
            + File.separator + "flink-min");

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptions.TARGET, KubernetesDeploymentTarget.APPLICATION.getName());
        flinkConfig.set(PipelineOptions.NAME, submitJobReq.getAppName());

        if (WorkType.FLINK_JAR.equals(submitJobReq.getWorkType())) {
            flinkConfig.set(ApplicationConfiguration.APPLICATION_ARGS, submitJobReq.getProgramArgs());
        } else {
            flinkConfig.set(ApplicationConfiguration.APPLICATION_ARGS, Collections.singletonList(
                Base64.getEncoder().encodeToString(JSON.toJSONString(submitJobReq.getAcornPluginReq()).getBytes())));
        }
        flinkConfig.set(ApplicationConfiguration.APPLICATION_MAIN_CLASS, submitJobReq.getEntryClass());
        flinkConfig.set(PipelineOptions.JARS, Collections.singletonList("local:///opt/flink/examples/app.jar"));
        flinkConfig.set(KubernetesConfigOptions.CLUSTER_ID, "zhihuiyun-cluster-" + System.currentTimeMillis());
        flinkConfig.set(KubernetesConfigOptions.REST_SERVICE_EXPOSED_TYPE,
            KubernetesConfigOptions.ServiceExposedType.NodePort);
        flinkConfig.set(KubernetesConfigOptions.CONTAINER_IMAGE_PULL_POLICY,
            KubernetesConfigOptions.ImagePullPolicy.IfNotPresent);
        flinkConfig.set(KubernetesConfigOptions.NAMESPACE, "zhihuiyun-space");
        flinkConfig.set(KubernetesConfigOptions.KUBERNETES_SERVICE_ACCOUNT, "zhihuiyun");
        flinkConfig.set(KubernetesConfigOptions.CONTAINER_IMAGE, "flink:1.18.1-scala_2.12");
        flinkConfig.set(KubernetesConfigOptions.TASK_MANAGER_CPU, 2.0);

        String podTemplate = "apiVersion: v1\n" + "kind: Pod\n" + "metadata:\n" + "  name: pod-template\n" + "spec:\n"
            + "  containers:\n" + "    - name: flink-main-container\n" + "      volumeMounts:\n" + "%s" + "  volumes:\n"
            + "%s";

        List<String> volumeMounts = new ArrayList<>();
        volumeMounts.add("        - name: app\n" + "          mountPath: /opt/flink/examples/app.jar\n");
        volumeMounts.add("       - name: flink-log\n" + "          mountPath: /log\n");

        List<String> volumes = new ArrayList<>();

        if (WorkType.FLINK_JAR.equals(submitJobReq.getWorkType())) {
            volumes.add("    - name: app\n" + "      hostPath:\n" + "        path: " + submitJobReq.getAgentHomePath()
                + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "file" + File.separator
                + submitJobReq.getAppResource() + "\n");
        } else {
            volumes.add("    - name: app\n" + "      hostPath:\n" + "        path: " + submitJobReq.getAgentHomePath()
                + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "plugins" + File.separator
                + submitJobReq.getAppResource() + "\n");
        }

        volumes.add("   - name: flink-log\n" + "      hostPath:\n" + "        path: " + submitJobReq.getAgentHomePath()
            + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "k8s-logs" + File.separator
            + submitJobReq.getWorkInstanceId() + "\n");

        File[] jarFiles = new File(
            submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "lib")
                .listFiles();
        if (jarFiles != null) {
            for (File jarFile : jarFiles) {
                if (jarFile.getName().contains("fastjson") || jarFile.getName().contains("flink")
                    || jarFile.getName().contains("connector")) {
                    volumeMounts.add("       - name: " + jarFile.getName().replace(".", "-") + "\n"
                        + "          mountPath: /opt/flink/lib/" + jarFile.getName() + "\n");
                    volumes.add("   - name: " + jarFile.getName().replace(".", "-") + "\n" + "      hostPath:\n"
                        + "        path: " + submitJobReq.getAgentHomePath() + File.separator
                        + PathConstants.AGENT_PATH_NAME + File.separator + "lib" + File.separator + jarFile.getName()
                        + "\n");
                }
            }
        }

        String podTemplateContent =
            String.format(podTemplate, Strings.join(volumeMounts, ' '), Strings.join(volumes, ' '));

        // 判断pod文件夹是否存在
        if (!new File(
            submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "pod")
                .exists()) {
            try {
                Files.createDirectories(Paths.get(submitJobReq.getAgentHomePath() + File.separator
                    + PathConstants.AGENT_PATH_NAME + File.separator + "pod"));
            } catch (IOException e) {
                throw new AgentResponseException(e.getMessage());
            }
        }

        // 判断k8s-logs文件夹是否存在
        if (!new File(submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator
            + "k8s-logs").exists()) {
            try {
                Files.createDirectories(Paths.get(submitJobReq.getAgentHomePath() + File.separator
                    + PathConstants.AGENT_PATH_NAME + File.separator + "k8s-logs"));
            } catch (IOException e) {
                throw new AgentResponseException(e.getMessage());
            }
        }

        // 先把许可证保存下来
        try (InputStream inputStream = new ByteArrayInputStream(podTemplateContent.getBytes())) {
            Files.copy(inputStream,
                Paths.get(submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME
                    + File.separator + "pod").resolve(submitJobReq.getWorkInstanceId() + ".yaml"),
                StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AgentResponseException("生成pod文件失败");
        }

        flinkConfig.set(KubernetesConfigOptions.KUBERNETES_POD_TEMPLATE,
            submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "pod"
                + File.separator + submitJobReq.getWorkInstanceId() + ".yaml");

        flinkConfig.set(KubernetesConfigOptions.FLINK_LOG_DIR, "/log");
        flinkConfig.set(DeploymentOptionsInternal.CONF_DIR, submitJobReq.getFlinkHome() + "/conf");
        flinkConfig.set(JobManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1g"));
        flinkConfig.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1g"));
        flinkConfig.set(TaskManagerOptions.NUM_TASK_SLOTS, 1);
        flinkConfig.set(KubernetesConfigOptions.KUBERNETES_HOSTNETWORK_ENABLED, true);

        ClusterSpecification clusterSpecification =
            new ClusterSpecification.ClusterSpecificationBuilder().setMasterMemoryMB(1024).setTaskManagerMemoryMB(1024)
                .setSlotsPerTaskManager(2).createClusterSpecification();

        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.fromConfiguration(flinkConfig);
        applicationConfiguration.applyToConfiguration(flinkConfig);
        KubernetesClusterClientFactory kubernetesClusterClientFactory = new KubernetesClusterClientFactory();
        try (KubernetesClusterDescriptor clusterDescriptor =
            kubernetesClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            ClusterClientProvider<String> clusterClientProvider =
                clusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
            return SubmitJobRes.builder().webUrl(clusterClientProvider.getClusterClient().getWebInterfaceURL())
                .jobId(String.valueOf(clusterClientProvider.getClusterClient().getClusterId())).build();
        } catch (Exception e) {
            throw new AgentResponseException("提交任务失败" + e.getCause().getMessage());
        }
    }

    @Override
    public GetJobInfoRes getJobInfo(GetJobInfoReq getJobInfoReq) {

        String getStatusJobManagerFormat = "kubectl get pods -l app=%s -n zhihuiyun-space";
        String line;
        StringBuilder errLog = new StringBuilder();

        try {
            String command = String.format(getStatusJobManagerFormat, getJobInfoReq.getJobId());
            Process process = Runtime.getRuntime().exec(command);
            try (InputStream inputStream = process.getInputStream();
                InputStream errStream = process.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                BufferedReader errReader =
                    new BufferedReader(new InputStreamReader(errStream, StandardCharsets.UTF_8))) {

                while ((line = reader.readLine()) != null) {
                    errLog.append(line).append("\n");
                    String pattern = "\\s+\\d/\\d\\s+(\\w+)";
                    Pattern regex = Pattern.compile(pattern);
                    Matcher matcher = regex.matcher(line);
                    if (matcher.find()) {
                        return GetJobInfoRes.builder().status(matcher.group(1)).jobId(getJobInfoReq.getJobId()).build();
                    }
                }

                if (errLog.toString().isEmpty()) {
                    while ((line = errReader.readLine()) != null) {
                        errLog.append(line).append("\n");
                    }
                    if (errLog.toString().contains("No resources found in zhihuiyun-space namespace")) {
                        return GetJobInfoRes.builder().status("FINISHED").jobId(getJobInfoReq.getJobId()).build();
                    }
                }
                int exitCode = process.waitFor();
                if (exitCode == 1) {
                    throw new AgentResponseException("Command execution failed:\n" + errLog);
                }
            } catch (IOException | InterruptedException e) {
                throw new AgentResponseException(e.getMessage());
            }
        } catch (IOException e) {
            throw new AgentResponseException(e.getMessage());
        }

        throw new AgentResponseException("获取状态异常");
    }

    @Override
    public GetJobLogRes getJobLog(GetJobLogReq getJobLogReq) {

        File[] logFiles = new File(getJobLogReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME
            + File.separator + "k8s-logs" + File.separator + getJobLogReq.getWorkInstanceId()).listFiles();

        StringBuilder logBuilder = new StringBuilder();
        if (logFiles != null) {
            for (File logFile : logFiles) {
                if (logFile.getName().contains("taskmanager")) {
                    try {
                        FileReader fileReader = new FileReader(logFile);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            logBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        fileReader.close();
                    } catch (IOException e) {
                        throw new AgentResponseException(e.getMessage());
                    }
                    break;
                }
                if (logFile.getName().contains("application")) {
                    try {
                        FileReader fileReader = new FileReader(logFile);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            logBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        fileReader.close();
                    } catch (IOException e) {
                        throw new AgentResponseException(e.getMessage());
                    }
                    break;
                }
            }
        }

        return GetJobLogRes.builder().log(logBuilder.toString()).build();
    }

    @Override
    public StopJobRes stopJobReq(StopJobReq stopJobReq) {

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptions.TARGET, KubernetesDeploymentTarget.APPLICATION.getName());
        flinkConfig.set(KubernetesConfigOptions.NAMESPACE, "zhihuiyun-space");
        flinkConfig.set(KubernetesConfigOptions.KUBERNETES_SERVICE_ACCOUNT, "zhihuiyun");

        KubernetesClusterClientFactory kubernetesClusterClientFactory = new KubernetesClusterClientFactory();
        try (KubernetesClusterDescriptor clusterDescriptor =
            kubernetesClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            clusterDescriptor.killCluster(stopJobReq.getJobId());
            return StopJobRes.builder().build();
        } catch (Exception e) {
            throw new AgentResponseException("提交任务失败" + e.getMessage());
        }
    }
}
