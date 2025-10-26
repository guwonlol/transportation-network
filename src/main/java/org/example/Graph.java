package org.example;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    int V, E;
    List<Edge> edges;

    public Graph(int vertices) {
        this.V = vertices;
        this.E = 0;
        edges = new ArrayList<>();
    }

    public void addEdge(int src, int dest, double weight) {
        edges.add(new Edge(src, dest, weight));
        E++;
    }
}