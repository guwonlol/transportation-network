package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

public class Prim {
    public static class Result {
        List<Edge> mstEdges;
        double totalCost;
        long operationCount;
        long executionTime;

        Result() {
            mstEdges = new ArrayList<>();
            totalCost = 0;
            operationCount = 0;
        }
    }

    public static Result primMST(Graph graph) {
        long startTime = System.nanoTime();
        Result result = new Result();

        boolean[] inMST = new boolean[graph.V];
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> Double.compare(a.weight, b.weight));
        double[] key = new double[graph.V];
        Arrays.fill(key, Double.POSITIVE_INFINITY);

        key[0] = 0;
        pq.offer(new Edge(-1, 0, 0));
        result.operationCount++;

        while (!pq.isEmpty()) {
            Edge edge = pq.poll();
            int u = edge.dest;
            result.operationCount++;

            if (inMST[u]) continue;

            inMST[u] = true;
            if (edge.src != -1) {
                result.mstEdges.add(edge);
                result.totalCost += edge.weight;
            }

            for (Edge e : graph.edges) {
                if ((e.src == u && !inMST[e.dest]) || (e.dest == u && !inMST[e.src])) {
                    int v = (e.src == u) ? e.dest : e.src;
                    if (key[v] > e.weight) {
                        key[v] = e.weight;
                        pq.offer(new Edge(u, v, e.weight));
                        result.operationCount++;
                    }
                }
            }
        }

        result.executionTime = (System.nanoTime() - startTime) / 1000000;
        return result;
    }
}