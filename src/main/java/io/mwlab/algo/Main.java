package io.mwlab.algo;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;

public class Main {
    private enum Vertex{ A, B, C, D, E, F, G, H }
    private record Edge(Vertex v1, Vertex v2){}
    public static void main(String[] args){

        var vertexes = Arrays.stream(Vertex.values()).toList();
        var edges    = List.of(
            new Edge(Vertex.A, Vertex.B),
            new Edge(Vertex.A, Vertex.D),
            new Edge(Vertex.A, Vertex.H),
            new Edge(Vertex.B, Vertex.C),
            new Edge(Vertex.B, Vertex.D),
            new Edge(Vertex.C, Vertex.D),
            new Edge(Vertex.C, Vertex.F),
            new Edge(Vertex.D, Vertex.E),
            new Edge(Vertex.E, Vertex.F),
            new Edge(Vertex.E, Vertex.H),
            new Edge(Vertex.F, Vertex.G),
            new Edge(Vertex.G, Vertex.H)
        );

        printGraph(vertexes, edges);
        // a. Write a function that returns all the possible paths between A-H.
        var resultList = allPossiblePaths(vertexes, edges, Vertex.A, Vertex.H);
        printAllPossiblePaths(resultList);
        // b. Write a function that returns the least number of hops (shortest path) between A-H.
        printLeastNumberOfHops(resultList);

    }


    // a. Write a function that returns all the possible paths between A-H.
    private static List<List<Vertex>> allPossiblePaths(
        Collection<Vertex>  vertexes,
        Collection<Edge>    edges,
        Vertex              start,
        Vertex              end
    ){
        var resultList = new ArrayList<List<Vertex>>();
        allPossiblePaths(vertexes, edges, start, end, resultList);
        return resultList.stream().distinct().toList();
    }
    private static void allPossiblePaths(
        Collection<Vertex>  vertexes,
        Collection<Edge>    edges,
        Vertex              start,
        Vertex              end,
        List<List<Vertex>>  resultList
    ){
        var graph = new DefaultUndirectedGraph<Vertex, DefaultEdge>(DefaultEdge.class);
        vertexes.forEach(graph::addVertex);
        edges.forEach(edge -> graph.addEdge(edge.v1, edge.v2));
        var algorithm = new DijkstraShortestPath<>(graph);
        var path = algorithm.getPath(start, end);
        if (isNull(path)){
            return;
        }
        var vertexList = path.getVertexList();
        resultList.add(vertexList);

        for ( int i = 0; i < vertexList.size()-1; i++){
            var v1 = vertexList.get(i);
            var v2 = vertexList.get(i+1);
            var usedEdgeA = new Edge(v1, v2);
            var usedEdgeB = new Edge(v2, v1);
            var newEdges = new ArrayList<>(List.copyOf(edges));
            newEdges.remove(usedEdgeA);
            newEdges.remove(usedEdgeB);
            allPossiblePaths(vertexes, newEdges, start, end, resultList);
        }
    }

    // b. Write a function that returns the least number of hops (shortest path) between A-H.
    private static void printLeastNumberOfHops(List<List<Vertex>> resultList){
        var shortestPath = resultList.stream().min(comparingInt(List::size)).orElse(emptyList());
        var shortestPathStr = shortestPath.stream().map(Vertex::name).collect(joining(","));
        System.out.printf("The shortest path: '%s', the number of hops '%d'.\n", shortestPathStr, shortestPath.size());
    }


    private static void printGraph(
            Collection<Vertex>  vertexes,
            Collection<Edge>    edges
    ){
        var graph = new DefaultUndirectedGraph<Vertex, DefaultEdge>(DefaultEdge.class);
        vertexes.forEach(graph::addVertex);
        edges.forEach(edge -> graph.addEdge(edge.v1, edge.v2));
        var exporter = new DOTExporter<Vertex, DefaultEdge>(Enum::name);
        try (var writer = new StringWriter()){
            exporter.exportGraph(graph, writer);
            System.out.println(writer);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void printAllPossiblePaths(List<List<Vertex>>  resultList){
        resultList.stream()
            .map(vertexList -> vertexList.stream().map(Vertex::name).collect(joining(",")))
            .forEach(System.out::println);
    }
}

