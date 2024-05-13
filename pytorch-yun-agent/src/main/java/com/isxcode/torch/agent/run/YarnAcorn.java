package com.isxcode.torch.agent.run;

import com.alibaba.fastjson2.JSON;
import com.isxcode.torch.api.agent.pojos.req.GetJobInfoReq;
import com.isxcode.torch.api.agent.pojos.req.GetJobLogReq;
import com.isxcode.torch.api.agent.pojos.req.StopJobReq;
import com.isxcode.torch.api.agent.pojos.req.SubmitJobReq;
import com.isxcode.torch.api.agent.pojos.res.*;
import com.isxcode.torch.api.api.constants.PathConstants;
import com.isxcode.torch.backend.api.base.exceptions.AgentResponseException;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.*;
import org.apache.flink.yarn.YarnClusterClientFactory;
import org.apache.flink.yarn.YarnClusterDescriptor;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.apache.flink.yarn.configuration.YarnDeploymentTarget;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

@Service
@Slf4j
@RequiredArgsConstructor
public class YarnAcorn implements AcornRun {

    @Override
    public SubmitJobRes submitJob(SubmitJobReq submitJobReq) {

        submitJobReq.setFlinkHome(submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME
            + File.separator + "flink-min");

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptions.TARGET, YarnDeploymentTarget.APPLICATION.getName());
        flinkConfig.set(PipelineOptions.NAME, submitJobReq.getAppName());
        flinkConfig.set(ApplicationConfiguration.APPLICATION_ARGS, singletonList(
            Base64.getEncoder().encodeToString(JSON.toJSONString(submitJobReq.getAcornPluginReq()).getBytes())));
        flinkConfig.set(ApplicationConfiguration.APPLICATION_MAIN_CLASS, submitJobReq.getEntryClass());
        flinkConfig.set(PipelineOptions.JARS,
            singletonList(submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME
                + File.separator + "plugins" + File.separator + submitJobReq.getAppResource()));
        flinkConfig.set(DeploymentOptionsInternal.CONF_DIR, submitJobReq.getFlinkHome() + "/conf");
        flinkConfig.set(JobManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1g"));
        flinkConfig.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, MemorySize.parse("1g"));
        flinkConfig.set(TaskManagerOptions.NUM_TASK_SLOTS, 1);
        flinkConfig.set(YarnConfigOptions.APPLICATION_NAME, submitJobReq.getAppName());
        Path path = new Path(System.getenv("HADOOP_CONF_DIR") + "/yarn-site.xml");
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.addResource(path);
        Map<String, String> yarn = conf.getPropsWithPrefix("yarn");
        yarn.forEach((k, v) -> {
            flinkConfig.setString("flink.yarn" + k, v);
        });
        flinkConfig.set(YarnConfigOptions.FLINK_DIST_JAR, submitJobReq.getFlinkHome() + "/lib/flink-dist-1.18.1.jar");
        List<String> libFile = new ArrayList<>();
        libFile.add(submitJobReq.getFlinkHome() + "/lib");
        libFile.add(
            submitJobReq.getAgentHomePath() + File.separator + PathConstants.AGENT_PATH_NAME + File.separator + "/lib");
        flinkConfig.set(YarnConfigOptions.SHIP_FILES, libFile);

        ClusterSpecification clusterSpecification =
            new ClusterSpecification.ClusterSpecificationBuilder().setMasterMemoryMB(1024).setTaskManagerMemoryMB(1024)
                .setSlotsPerTaskManager(1).createClusterSpecification();

        ApplicationConfiguration applicationConfiguration = ApplicationConfiguration.fromConfiguration(flinkConfig);
        YarnClusterClientFactory yarnClusterClientFactory = new YarnClusterClientFactory();
        try (YarnClusterDescriptor clusterDescriptor = yarnClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            ClusterClientProvider<ApplicationId> applicationIdClusterClientProvider =
                clusterDescriptor.deployApplicationCluster(clusterSpecification, applicationConfiguration);
            return SubmitJobRes.builder()
                .jobId(String.valueOf(applicationIdClusterClientProvider.getClusterClient().getClusterId())).build();
        } catch (Exception e) {
            throw new AgentResponseException("提交任务失败" + e.getMessage());
        }
    }

    @Override
    public GetJobInfoRes getJobInfo(GetJobInfoReq getJobInfoReq) {

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptionsInternal.CONF_DIR, getJobInfoReq.getFlinkHome() + "/conf");
        Path path = new Path(System.getenv("HADOOP_CONF_DIR") + "/yarn-site.xml");
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.addResource(path);
        Map<String, String> yarn = conf.getPropsWithPrefix("yarn");
        yarn.forEach((k, v) -> {
            flinkConfig.setString("flink.yarn" + k, v);
        });

        YarnClusterClientFactory yarnClusterClientFactory = new YarnClusterClientFactory();
        try (YarnClusterDescriptor clusterDescriptor = yarnClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            ApplicationReport applicationReport = clusterDescriptor.getYarnClient()
                .getApplicationReport(ApplicationId.fromString(getJobInfoReq.getJobId()));
            return GetJobInfoRes.builder().jobId(getJobInfoReq.getJobId())
                .status(applicationReport.getYarnApplicationState().toString()).build();
        } catch (Exception e) {
            throw new AgentResponseException("提交任务失败" + e.getMessage());
        }
    }

    @Override
    public GetJobLogRes getJobLog(GetJobLogReq getJobLogReq) {

        String getLogCmdFormat = "yarn logs -applicationId %s";
        Process process;
        try {
            process = Runtime.getRuntime().exec(String.format(getLogCmdFormat, getJobLogReq.getJobId()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        StringBuilder errLog = new StringBuilder();
        String line;
        while (true) {
            try {
                if (!((line = reader.readLine()) != null))
                    break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            errLog.append(line).append("\n");
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode == 1) {
                throw new IsxAppException(errLog.toString());
            } else {
                Pattern regex =
                    Pattern.compile("LogType:taskmanager.log\\s*([\\s\\S]*?)\\s*End of LogType:taskmanager.log");
                Matcher matcher = regex.matcher(errLog);
                String log = "";
                while (matcher.find()) {
                    String tmpLog = matcher.group();
                    if (tmpLog.contains("ERROR")) {
                        log = tmpLog;
                        break;
                    }
                    if (tmpLog.length() > log.length()) {
                        log = tmpLog;
                    }
                }
                return GetJobLogRes.builder().log(log).build();
            }
        } catch (InterruptedException e) {
            throw new AgentResponseException(e.getMessage());
        }
    }

    @Override
    public StopJobRes stopJobReq(StopJobReq stopJobReq) {

        Configuration flinkConfig = GlobalConfiguration.loadConfiguration();
        flinkConfig.set(DeploymentOptionsInternal.CONF_DIR, stopJobReq.getFlinkHome() + "/conf");
        Path path = new Path(System.getenv("HADOOP_CONF_DIR") + "/yarn-site.xml");
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.addResource(path);
        Map<String, String> yarn = conf.getPropsWithPrefix("yarn");
        yarn.forEach((k, v) -> {
            flinkConfig.setString("flink.yarn" + k, v);
        });

        YarnClusterClientFactory yarnClusterClientFactory = new YarnClusterClientFactory();
        try (YarnClusterDescriptor clusterDescriptor = yarnClusterClientFactory.createClusterDescriptor(flinkConfig)) {
            clusterDescriptor.getYarnClient().killApplication(ApplicationId.fromString(stopJobReq.getJobId()));
            return StopJobRes.builder().build();
        } catch (Exception e) {
            throw new AgentResponseException("停止任务失败" + e.getMessage());
        }
    }
}
