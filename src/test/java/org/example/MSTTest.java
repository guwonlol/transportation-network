package org.example;

import org.junit.Test;
import java.util.*;

import static org.junit.Assert.*;

public class MSTTest {
    @Test
    public void testMST() {
        List<Graph> graphs = new ArrayList<>();
        List<Prim.Result> primResults = new ArrayList<>();
        List<Kruskal.Result> kruskalResults = new ArrayList<>();

        // Create a small test graph
        Graph smallGraph = new Graph(4);
        smallGraph.addEdge(0, 1, 1.0);
        smallGraph.addEdge(0, 2, 3.0);
        smallGraph.addEdge(1, 2, 3.0);
        smallGraph.addEdge(1, 3, 2.0);
        smallGraph.addEdge(2, 3, 4.0);
        graphs.add(smallGraph);

        primResults.add(Prim.primMST(smallGraph));
        kruskalResults.add(Kruskal.kruskalMST(smallGraph));

        runTests(graphs, primResults, kruskalResults);
    }

    public static void runTests(List<Graph> graphs, List<Prim.Result> primResults, List<Kruskal.Result> kruskalResults) {
        for (int i = 0; i < graphs.size(); i++) {
            Graph g = graphs.get(i);
            Prim.Result prim = primResults.get(i);
            Kruskal.Result kruskal = kruskalResults.get(i);

            // Correctness tests
            assertEquals("Total cost mismatch for graph " + i, prim.totalCost, kruskal.totalCost, 0.0001);
            assertEquals("Prim MST edge count incorrect for graph " + i, g.V - 1, prim.mstEdges.size());
            assertEquals("Kruskal MST edge count incorrect for graph " + i, g.V - 1, kruskal.mstEdges.size());

            // Check for cycles
            Kruskal.UnionFind uf = new Kruskal.UnionFind(g.V);
            for (Edge e : prim.mstEdges) {
                assertTrue("Prim MST contains cycle for graph " + i, uf.union(e.src, e.dest));
            }
            uf = new Kruskal.UnionFind(g.V);
            for (Edge e : kruskal.mstEdges) {
                assertTrue("Kruskal MST contains cycle for graph " + i, uf.union(e.src, e.dest));
            }

            // Check connectivity
            Set<Integer> reachable = new HashSet<>();
            dfs(prim.mstEdges, 0, reachable);
            assertEquals("Prim MST not connected for graph " + i, g.V, reachable.size());
            reachable.clear();
            dfs(kruskal.mstEdges, 0, reachable);
            assertEquals("Kruskal MST not connected for graph " + i, g.V, reachable.size());

            // Performance tests
            assertTrue("Prim negative execution time for graph " + i, prim.executionTime >= 0);
            assertTrue("Kruskal negative execution time for graph " + i, kruskal.executionTime >= 0);
            assertTrue("Prim negative operation count for graph " + i, prim.operationCount >= 0);
            assertTrue("Kruskal negative operation count for graph " + i, kruskal.operationCount >= 0);
        }
        System.out.println("All tests passed!");
    }

    static void dfs(List<Edge> edges, int vertex, Set<Integer> visited) {
        visited.add(vertex);
        for (Edge e : edges) {
            if (e.src == vertex && !visited.contains(e.dest)) {
                dfs(edges, e.dest, visited);
            } else if (e.dest == vertex && !visited.contains(e.src)) {
                dfs(edges, e.src, visited);
            }
        }
    }
}