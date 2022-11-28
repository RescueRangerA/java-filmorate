package ru.yandex.practicum.filmorate.util;

import java.util.*;

public class Graph<T, U> {
    private final Set<T> vertexes;

    private final Map<T, Map<T, U>> edges;

    public Graph() {
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
    }

    public void removeEdge(T vertexA, T vertexB) {
        if (!vertexes.contains(vertexA) || !vertexes.contains(vertexB)) {
            return;
        }

        edges.get(vertexA).remove(vertexB);
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
        return (List<U>) edges
                .values()
                .stream()
                .map(Map::values)
                .reduce(new LinkedList<>(), (result, item) -> {
                    result.addAll(item);
                    return result;
                });
    }

    public Map<T, U> getEdgesOfVertex(T vertex) {
        return edges.containsKey(vertex) ? new HashMap<>(edges.get(vertex)) : new HashMap<>();
    }
}
