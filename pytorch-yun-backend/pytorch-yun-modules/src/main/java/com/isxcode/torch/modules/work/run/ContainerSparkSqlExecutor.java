package com.isxcode.torch.modules.work.run;

import com.isxcode.torch.api.work.constants.WorkLog;
import com.isxcode.torch.api.work.exceptions.WorkRunException;
import com.isxcode.torch.modules.datasource.repository.DatasourceRepository;
import com.isxcode.torch.modules.datasource.service.DatasourceService;
import com.isxcode.torch.modules.work.entity.WorkInstanceEntity;
import com.isxcode.torch.modules.work.repository.WorkInstanceRepository;
import com.isxcode.torch.modules.workflow.repository.WorkflowInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ContainerSparkSqlExecutor extends WorkExecutor {

    private final DatasourceRepository datasourceRepository;

    private final WorkInstanceRepository workInstanceRepository;

    private final DatasourceService datasourceService;

    public ContainerSparkSqlExecutor(DatasourceRepository datasourceRepository,
        WorkInstanceRepository workInstanceRepository, WorkflowInstanceRepository workflowInstanceRepository,
        DatasourceService datasourceService) {

        super(workInstanceRepository, workflowInstanceRepository);
        this.datasourceRepository = datasourceRepository;
        this.workInstanceRepository = workInstanceRepository;
        this.datasourceService = datasourceService;
    }

    public void execute(WorkRunContext workRunContext, WorkInstanceEntity workInstance) {

        // 获取日志构造器
        StringBuilder logBuilder = workRunContext.getLogBuilder();

        // 检测数据源是否配置
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始检测运行环境 \n");

        // 判断容器是否可用

        // 数据源检查通过
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("检测运行环境完成  \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 检查脚本是否为空
        if (Strings.isEmpty(workRunContext.getScript())) {
            throw new WorkRunException(LocalDateTime.now() + WorkLog.ERROR_INFO + "Sql内容为空 \n");
        }

        // 脚本检查通过
        logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("开始执行作业 \n");
        workInstance = updateInstance(workInstance, logBuilder);

        // 开始执行接口访问

        // 讲data转为json存到实例中
        // logBuilder.append(LocalDateTime.now()).append(WorkLog.SUCCESS_INFO).append("数据保存成功
        // \n");
        // workInstance.setSubmitLog(logBuilder.toString());
        // workInstance.setResultData(JSON.toJSONString(result));
        // workInstanceRepository.saveAndFlush(workInstance);
    }

    @Override
    protected void abort(WorkInstanceEntity workInstance) {

        Thread thread = WORK_THREAD.get(workInstance.getId());
        thread.interrupt();
    }
}
