package com.isxcode.torch.common.utils.jgrapht;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class JgraphtUtils {

    public static void isCycle(List<String> nodeIdList, List<List<String>> flowList) {

        Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        nodeIdList.forEach(graph::addVertex);
        flowList.forEach(e -> graph.addEdge(e.get(0), e.get(1)));
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(graph);
        if (cycleDetector.detectCycles()) {
            throw new IsxAppException("工作流闭环了");
        }
    }
}
