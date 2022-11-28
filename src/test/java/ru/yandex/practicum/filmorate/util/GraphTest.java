package ru.yandex.practicum.filmorate.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

class GraphTest {

    @Test
    void addVertex() {
        Graph<Long, Boolean> graph = new Graph<>();

        graph.addVertex(1L);
        graph.addVertex(2L);

        Assertions.assertEquals(Set.of(1L, 2L), graph.getVertexes());
    }

    @Test
    void removeVertex() {
        Graph<Long, Boolean> graph = new Graph<>();

        graph.addVertex(1L);
        graph.addVertex(2L);
        graph.removeVertex(2L);

        Assertions.assertEquals(Set.of(1L), graph.getVertexes());
    }

    @Test
    void addAndGetEdge() {
        Graph<Long, Boolean> graph = new Graph<>();

        graph.addVertex(1L);
        graph.addVertex(2L);
        graph.addEdge(1L, 2L, true);
        Assertions.assertEquals(true, graph.getEdge(1L, 2L));
        Assertions.assertNull(graph.getEdge(2L, 1L));
    }

    @Test
    void addEdgeToNonExistingVertex() {
        Graph<Long, Boolean> graph = new Graph<>();

        graph.addVertex(1L);
        graph.addEdge(1L, 2L, true);
        Assertions.assertNull(graph.getEdge(1L, 2L));
    }

    @Test
    void addAndGetEqualEdge() {
        Graph<Long, Boolean> graph = new Graph<>();

        graph.addVertex(1L);
        graph.addEdge(1L, 1L, true);
        Assertions.assertNull(graph.getEdge(1L, 2L));
    }

    @Test
    void removeEdge() {
        Graph<Long, Boolean> graph = new Graph<>();

        graph.addVertex(1L);
        graph.addVertex(2L);
        graph.addEdge(1L, 2L, true);
        Assertions.assertEquals(true, graph.getEdge(1L, 2L));
        graph.removeEdge(1L, 2L);
        Assertions.assertNull(graph.getEdge(1L, 2L));
    }

    @Test
    void getEdgesOfVertex() {
        Graph<Long, Boolean> graph = new Graph<>();

        graph.addVertex(1L);
        graph.addVertex(2L);
        graph.addVertex(3L);
        graph.addEdge(1L, 2L, true);
        graph.addEdge(1L, 3L, false);
        Assertions.assertEquals(Map.of(2L, true, 3L, false), graph.getEdgesOfVertex(1L));
    }


    @Test
    void getEdges() {
        Graph<Long, Boolean> graph = new Graph<>();

        graph.addVertex(1L);
        graph.addVertex(2L);
        graph.addEdge(1L, 2L, true);
        Assertions.assertEquals(1, graph.getEdges().size());
    }
}