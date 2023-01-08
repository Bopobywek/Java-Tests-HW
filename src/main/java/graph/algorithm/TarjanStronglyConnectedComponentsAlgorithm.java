package graph.algorithm;

import java.util.*;

public class TarjanStronglyConnectedComponentsAlgorithm<T> {
    private Map<T, Integer> indexesOfVertexes = new HashMap<>();
    private Map<T, Integer> lowlinkOfVertexes = new HashMap<>();
    private Set<T> onStack = new HashSet<>();
    private Stack<T> stack = new Stack<>();
    private Map<T, List<T>> graphRepresentation;
    private List<List<T>> result = new ArrayList<>();
    private int index = 0;

    public List<List<T>> findStronglyConnectedComponents(Map<T, List<T>> graphRepresentation) {
        this.graphRepresentation = graphRepresentation;
        for (var vertex : graphRepresentation.keySet()) {
            if (!indexesOfVertexes.containsKey(vertex)) {
                find(vertex);
            }
        }

        return result;
    }

    private void find(T vertex) {
        indexesOfVertexes.put(vertex, index);
        lowlinkOfVertexes.put(vertex, index);
        index += 1;
        stack.push(vertex);
        onStack.add(vertex);

        for (var neighbour : graphRepresentation.get(vertex)) {
            if (!indexesOfVertexes.containsKey(neighbour)) {
                find(neighbour);
                lowlinkOfVertexes.put(vertex, Math.min(lowlinkOfVertexes.get(vertex), lowlinkOfVertexes.get(neighbour)));
            } else if (onStack.contains(neighbour)) {
                lowlinkOfVertexes.put(vertex, Math.min(lowlinkOfVertexes.get(vertex), indexesOfVertexes.get(neighbour)));
            }
        }

        if (lowlinkOfVertexes.get(vertex).equals(indexesOfVertexes.get(vertex))) {
            List<T> component = new ArrayList<>();
            T last;
            do {
                last = stack.pop();
                onStack.remove(last);
                component.add(last);
            } while (last != vertex);
            result.add(component);
        }

    }
}
