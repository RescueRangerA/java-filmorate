package ru.yandex.practicum.filmorate.util;

import java.util.*;

public class BiDirectedGraph<T, U> {
    private final Set<T> vertexes;

    private final Map<T, Map<T, U>> edges;

    public BiDirectedGraph() {
        edges = new HashMap<>();
        vertexes = new HashSet<>();
    }

    public void addVertex(T vertex) {
        if (vertexes.contains(vertex)) {
            return;
        }

        vertexes.add(vertex);
        edges.put(vertex, new HashMap<>());
    }

    public void removeVertex(T vertex) {
        vertexes.remove(vertex);
        edges.remove(vertex);
        edges.values().forEach((item) -> item.remove(vertex));
    }

    public void addEdge(T vertexA, T vertexB, U edge) {
        if (!vertexes.contains(vertexA) || !vertexes.contains(vertexB) || Objects.equals(vertexA, vertexB)) {
            return;
        }

        edges.get(vertexA).put(vertexB, edge);
        edges.get(vertexB).put(vertexA, edge);
    }

    public void removeEdge(T vertexA, T vertexB) {
        if (!vertexes.contains(vertexA) || !vertexes.contains(vertexB)) {
            return;
        }

        edges.get(vertexA).remove(vertexB);
        edges.get(vertexB).remove(vertexA);
    }

    public U getEdge(T vertexA, T vertexB) {
        if (!vertexes.contains(vertexA) || !vertexes.contains(vertexB)) {
            return null;
        }

        return edges.get(vertexA).get(vertexB);
    }

    public Set<T> getVertexes() {
        return new HashSet<>(vertexes);
    }

    public List<U> getEdges() {
        List<T> listOfVertexes = new ArrayList<>(vertexes);
        List<U> listOfAllEdges = new LinkedList<>();

        for (int i = 0; i < listOfVertexes.size(); i++) {
            for (int j = i + 1; j < listOfVertexes.size(); j++) {
                T vertexI = listOfVertexes.get(i);
                T vertexJ = listOfVertexes.get(j);
                U edge = edges.get(vertexI).get(vertexJ);

                if (edge != null) {
                    listOfAllEdges.add(edge);
                }
            }
        }

        return listOfAllEdges;
    }

    public Map<T, U> getEdgesOfVertex(T vertex) {
        return edges.containsKey(vertex) ? new HashMap<>(edges.get(vertex)) : new HashMap<>();
    }
}
