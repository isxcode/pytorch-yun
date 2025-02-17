package com.isxcode.torch.modules.work.run;

import com.isxcode.torch.api.work.constants.WorkType;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 执行器工厂类，返回对应作业的执行器.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class WorkExecutorFactory {

    private final ApplicationContext applicationContext;

    public WorkExecutor create(String workType) {

        switch (workType) {
            case WorkType.EXECUTE_FLINK_SQL:
                return applicationContext.getBean(FlinkSqlExecutor.class);
            case WorkType.QUERY_JDBC_SQL:
                return applicationContext.getBean(QuerySqlExecutor.class);
            case WorkType.EXECUTE_JDBC_SQL:
                return applicationContext.getBean(ExecuteSqlExecutor.class);
            case WorkType.DATA_SYNC_JDBC:
                return applicationContext.getBean(SyncWorkExecutor.class);
            case WorkType.BASH:
                return applicationContext.getBean(BashExecutor.class);
            case WorkType.PYTHON:
                return applicationContext.getBean(PythonExecutor.class);
            case WorkType.FLINK_JAR:
                return applicationContext.getBean(FlinkJarExecutor.class);
            case WorkType.SPARK_CONTAINER_SQL:
                return applicationContext.getBean(SparkContainerSqlExecutor.class);
            case WorkType.API:
                return applicationContext.getBean(ApiExecutor.class);
            default:
                throw new IsxAppException("作业类型不存在");
        }
    }
}
