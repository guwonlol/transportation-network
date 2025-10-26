package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Kruskal {
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

    static class UnionFind {
        int[] parent, rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        boolean union(int x, int y) {
            int px = find(x), py = find(y);
            if (px == py) return false;
            if (rank[px] < rank[py]) parent[px] = py;
            else if (rank[px] > rank[py]) parent[py] = px;
            else {
                parent[py] = px;
                rank[px]++;
            }
            return true;
        }
    }

    public static Result kruskalMST(Graph graph) {
        long startTime = System.nanoTime();
        Result result = new Result();

        List<Edge> sortedEdges = new ArrayList<>(graph.edges);
        Collections.sort(sortedEdges, (a, b) -> Double.compare(a.weight, b.weight));
        UnionFind uf = new UnionFind(graph.V);

        for (Edge edge : sortedEdges) {
            result.operationCount++;
            if (uf.union(edge.src, edge.dest)) {
                result.mstEdges.add(edge);
                result.totalCost += edge.weight;
            }
        }

        result.executionTime = (System.nanoTime() - startTime) / 1000000;
        return result;
    }
}