package org.example;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {
    public static class GraphData {
        Graph graph;
        List<String> nodeLabels;
        Map<String, Integer> labelToIndex;
        Map<Integer, String> indexToLabel;

        GraphData(Graph graph, List<String> nodeLabels, Map<String, Integer> labelToIndex, Map<Integer, String> indexToLabel) {
            this.graph = graph;
            this.nodeLabels = nodeLabels;
            this.labelToIndex = labelToIndex;
            this.indexToLabel = indexToLabel;
        }
    }

    public static List<GraphData> readInput(String filename) throws Exception {
        List<GraphData> graphs = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject input = (JSONObject) parser.parse(new FileReader(filename));
        JSONArray jsonGraphs = (JSONArray) input.get("graphs");

        for (Object obj : jsonGraphs) {
            JSONObject jsonGraph = (JSONObject) obj;
            JSONArray nodes = (JSONArray) jsonGraph.get("nodes");
            List<String> nodeLabels = new ArrayList<>();
            Map<String, Integer> labelToIndex = new HashMap<>();
            Map<Integer, String> indexToLabel = new HashMap<>();

            // Map node labels to indices
            for (int i = 0; i < nodes.size(); i++) {
                String label = (String) nodes.get(i);
                nodeLabels.add(label);
                labelToIndex.put(label, i);
                indexToLabel.put(i, label);
            }

            Graph graph = new Graph(nodes.size());
            JSONArray edges = (JSONArray) jsonGraph.get("edges");

            for (Object edgeObj : edges) {
                JSONObject edge = (JSONObject) edgeObj;
                String from = (String) edge.get("from");
                String to = (String) edge.get("to");
                double weight = ((Number) edge.get("weight")).doubleValue();
                int src = labelToIndex.get(from);
                int dest = labelToIndex.get(to);
                graph.addEdge(src, dest, weight);
            }
            graphs.add(new GraphData(graph, nodeLabels, labelToIndex, indexToLabel));
        }
        return graphs;
    }

    public static void writeOutput(String filename, List<GraphData> graphs, List<Prim.Result> primResults, List<Kruskal.Result> kruskalResults) throws Exception {
        JSONObject output = new JSONObject();
        JSONArray results = new JSONArray();

        for (int i = 0; i < graphs.size(); i++) {
            JSONObject result = new JSONObject();
            result.put("graph_id", i + 1);
            JSONObject inputStats = new JSONObject();
            inputStats.put("vertices", graphs.get(i).graph.V);
            inputStats.put("edges", graphs.get(i).graph.E);
            result.put("input_stats", inputStats);

            Map<Integer, String> indexToLabel = graphs.get(i).indexToLabel;

            JSONObject prim = new JSONObject();
            prim.put("mst_edges", primResults.get(i).mstEdges.stream()
                    .map(e -> Map.of("from", indexToLabel.get(e.src), "to", indexToLabel.get(e.dest), "weight", e.weight))
                    .collect(Collectors.toList()));
            prim.put("total_cost", primResults.get(i).totalCost);
            prim.put("operations_count", primResults.get(i).operationCount);
            prim.put("execution_time_ms", primResults.get(i).executionTime);

            JSONObject kruskal = new JSONObject();
            kruskal.put("mst_edges", kruskalResults.get(i).mstEdges.stream()
                    .map(e -> Map.of("from", indexToLabel.get(e.src), "to", indexToLabel.get(e.dest), "weight", e.weight))
                    .collect(Collectors.toList()));
            kruskal.put("total_cost", kruskalResults.get(i).totalCost);
            kruskal.put("operations_count", kruskalResults.get(i).operationCount);
            kruskal.put("execution_time_ms", kruskalResults.get(i).executionTime);

            result.put("prim", prim);
            result.put("kruskal", kruskal);
            results.add(result);
        }

        output.put("results", results);

        try (FileWriter file = new FileWriter(filename)) {
            file.write(output.toJSONString());
        }
    }

    public static void main(String[] args) throws Exception {
        // Generate sample input
        JSONObject input = new JSONObject();
        JSONArray graphs = new JSONArray();

        // Small graph (5 vertices)
        JSONObject smallGraph = new JSONObject();
        smallGraph.put("id", 1);
        JSONArray smallNodes = new JSONArray();
        smallNodes.addAll(List.of("A", "B", "C", "D", "E"));
        smallGraph.put("nodes", smallNodes);
        JSONArray smallEdges = new JSONArray();
        smallEdges.add(Map.of("from", "A", "to", "B", "weight", 2.0));
        smallEdges.add(Map.of("from", "A", "to", "C", "weight", 3.0));
        smallEdges.add(Map.of("from", "B", "to", "C", "weight", 1.0));
        smallEdges.add(Map.of("from", "B", "to", "D", "weight", 4.0));
        smallEdges.add(Map.of("from", "C", "to", "D", "weight", 5.0));
        smallEdges.add(Map.of("from", "D", "to", "E", "weight", 2.0));
        smallGraph.put("edges", smallEdges);
        graphs.add(smallGraph);

        // Medium graph (10 vertices)
        JSONObject mediumGraph = new JSONObject();
        mediumGraph.put("id", 2);
        JSONArray mediumNodes = new JSONArray();
        String[] mediumNodeLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        mediumNodes.addAll(List.of(mediumNodeLabels));
        mediumGraph.put("nodes", mediumNodes);
        JSONArray mediumEdges = new JSONArray();
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            for (int j = i + 1; j < 10; j++) {
                if (rand.nextDouble() < 0.4) {
                    mediumEdges.add(Map.of("from", mediumNodeLabels[i], "to", mediumNodeLabels[j], "weight", rand.nextDouble() * 10));
                }
            }
        }
        mediumGraph.put("edges", mediumEdges);
        graphs.add(mediumGraph);

        // Large graph (20 vertices)
        JSONObject largeGraph = new JSONObject();
        largeGraph.put("id", 3);
        JSONArray largeNodes = new JSONArray();
        String[] largeNodeLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"};
        largeNodes.addAll(List.of(largeNodeLabels));
        largeGraph.put("nodes", largeNodes);
        JSONArray largeEdges = new JSONArray();
        for (int i = 0; i < 20; i++) {
            for (int j = i + 1; j < 20; j++) {
                if (rand.nextDouble() < 0.3) {
                    largeEdges.add(Map.of("from", largeNodeLabels[i], "to", largeNodeLabels[j], "weight", rand.nextDouble() * 15));
                }
            }
        }
        largeGraph.put("edges", largeEdges);
        graphs.add(largeGraph);

        input.put("graphs", graphs);

        try (FileWriter file = new FileWriter("assign_3_input.json")) {
            file.write(input.toJSONString());
        }

        // Process graphs
        List<GraphData> graphDataList = readInput("assign_3_input.json");
        List<Prim.Result> primResults = new ArrayList<>();
        List<Kruskal.Result> kruskalResults = new ArrayList<>();

        for (GraphData graphData : graphDataList) {
            primResults.add(Prim.primMST(graphData.graph));
            kruskalResults.add(Kruskal.kruskalMST(graphData.graph));
        }

        // Write output
        writeOutput("assign_3_output.json", graphDataList, primResults, kruskalResults);
    }
}