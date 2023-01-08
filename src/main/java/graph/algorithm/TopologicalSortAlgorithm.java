package graph.algorithm;

import java.util.*;

public class TopologicalSortAlgorithm<T> {
    private Set<T> visited = new HashSet<>();
    private List<T> sortedObjects = new ArrayList<>();
    private Map<T, List<T>> graphRepresentation;

    private void runDepthFirstSearch(T key) {
        visited.add(key);
        for (T neighbour : graphRepresentation.get(key)) {
            if (!visited.contains(neighbour)) {
                runDepthFirstSearch(neighbour);
            }
        }

        sortedObjects.add(key);
    }

    public List<T> getSortedList(Map<T, List<T>> graphRepresentation) {
        this.graphRepresentation = graphRepresentation;

        for (T vertex : graphRepresentation.keySet()) {
            if (!visited.contains(vertex)) {
                runDepthFirstSearch(vertex);
            }
        }

        return sortedObjects;
    }
}
