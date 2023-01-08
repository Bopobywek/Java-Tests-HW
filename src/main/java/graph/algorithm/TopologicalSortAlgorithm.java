package graph.algorithm;

import java.util.*;

/**
 * Класс, реализующий алгоритм тополгоической сортировки.
 *
 * @param <T> тип значений-вершин в графе.
 */
public class TopologicalSortAlgorithm<T> {
    /**
     * Храним в множестве посещенные вершины.
     */
    private Set<T> visited = new HashSet<>();
    /**
     * Храним в списке отсортированные вершины.
     */
    private List<T> sortedObjects = new ArrayList<>();
    /**
     * Представление графа в виде списка смежности.
     */
    private Map<T, List<T>> graphRepresentation;

    /**
     * Восстанавливает состояние алгоритма к исходному.
     */
    private void resetToInitialState() {
        visited = new HashSet<>();
        sortedObjects = new ArrayList<>();
    }

    /**
     * Запускает обход в глубину от вершины {@code key}.
     *
     * @param key вершина, от которой запускается обход.
     */
    private void runDepthFirstSearch(T key) {
        visited.add(key);
        for (T neighbour : graphRepresentation.get(key)) {
            if (!visited.contains(neighbour)) {
                runDepthFirstSearch(neighbour);
            }
        }

        sortedObjects.add(key);
    }

    /**
     * Возвращает список вершин в топологическом порядке.
     *
     * @param graphRepresentation представление графа в виде списка смежности.
     * @return список вершин в топологическом порядке.
     */
    public List<T> getSortedList(Map<T, List<T>> graphRepresentation) {
        resetToInitialState();
        this.graphRepresentation = graphRepresentation;

        for (T vertex : graphRepresentation.keySet()) {
            if (!visited.contains(vertex)) {
                runDepthFirstSearch(vertex);
            }
        }

        return sortedObjects;
    }
}
