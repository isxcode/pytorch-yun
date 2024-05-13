package com.isxcode.torch.agent.service;

import com.isxcode.torch.agent.run.FlinkClusterAcorn;
import com.isxcode.torch.agent.run.KubernetesAcorn;
import com.isxcode.torch.agent.run.YarnAcorn;
import com.isxcode.torch.api.agent.constants.AgentType;
import com.isxcode.torch.api.agent.pojos.req.GetJobInfoReq;
import com.isxcode.torch.api.agent.pojos.req.GetJobLogReq;
import com.isxcode.torch.api.agent.pojos.req.StopJobReq;
import com.isxcode.torch.api.agent.pojos.req.SubmitJobReq;
import com.isxcode.torch.api.agent.pojos.res.GetJobInfoRes;
import com.isxcode.torch.api.agent.pojos.res.GetJobLogRes;
import com.isxcode.torch.api.agent.pojos.res.StopJobRes;
import com.isxcode.torch.api.agent.pojos.res.SubmitJobRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 代理服务层.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcornAgentBizService {

    private final FlinkClusterAcorn flinkClusterAcorn;

    private final KubernetesAcorn kubernetesAcorn;

    private final YarnAcorn yarnAcorn;

    public SubmitJobRes submitJob(SubmitJobReq submitJobReq) {

        switch (submitJobReq.getAgentType()) {
            case AgentType.YARN:
                return yarnAcorn.submitJob(submitJobReq);
            case AgentType.K8S:
                return kubernetesAcorn.submitJob(submitJobReq);
            case AgentType.FlinkCluster:
                return flinkClusterAcorn.submitJob(submitJobReq);
            default:
                throw new IsxAppException("agent类型不支持");
        }
    }

    public GetJobInfoRes getJobInfo(GetJobInfoReq getJobInfoReq) {

        switch (getJobInfoReq.getAgentType()) {
            case AgentType.YARN:
                return yarnAcorn.getJobInfo(getJobInfoReq);
            case AgentType.K8S:
                return kubernetesAcorn.getJobInfo(getJobInfoReq);
            case AgentType.FlinkCluster:
                return flinkClusterAcorn.getJobInfo(getJobInfoReq);
            default:
                throw new IsxAppException("agent类型不支持");
        }
    }

    public GetJobLogRes getJobLog(GetJobLogReq getJobLogReq) {

        switch (getJobLogReq.getAgentType()) {
            case AgentType.YARN:
                return yarnAcorn.getJobLog(getJobLogReq);
            case AgentType.K8S:
                return kubernetesAcorn.getJobLog(getJobLogReq);
            case AgentType.FlinkCluster:
                return flinkClusterAcorn.getJobLog(getJobLogReq);
            default:
                throw new IsxAppException("agent类型不支持");
        }
    }

    public StopJobRes stopJob(StopJobReq stopJobReq) {

        switch (stopJobReq.getAgentType()) {
            case AgentType.YARN:
                return yarnAcorn.stopJobReq(stopJobReq);
            case AgentType.K8S:
                return kubernetesAcorn.stopJobReq(stopJobReq);
            case AgentType.FlinkCluster:
                return flinkClusterAcorn.stopJobReq(stopJobReq);
            default:
                throw new IsxAppException("agent类型不支持");
        }

    }
}
