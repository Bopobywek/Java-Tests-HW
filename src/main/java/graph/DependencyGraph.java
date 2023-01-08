package graph;

import graph.algorithm.TarjanStronglyConnectedComponentsAlgorithm;
import graph.algorithm.TopologicalSortAlgorithm;

import java.util.*;

/**
 * Класс реализует граф зависимостей.
 * Интерфейс позаимсмтвован у Oracle <a href="https://docs.oracle.com/middleware/1213/jdev/api-reference-esdk/oracle/javatools/util/DependencyGraph.html">тут</a>
 * @param <T> тип объектов, которые хранит граф.
 */
public class DependencyGraph<T> {
    private final Map<T, List<T>> data = new HashMap<>();

    public void add(T key) {
        if (!data.containsKey(key)) {
            data.put(key, new ArrayList<>());
        }
    }
    public void addDependency(T dependent, T dependency) {
        add(dependent);
        add(dependency);
        data.get(dependent).add(dependency);
    }

    public void addDependencies(T dependent, Collection<T> dependencies) {
        for (T dependency : dependencies) {
            addDependency(dependent, dependency);
        }

        if (dependencies.isEmpty()) {
            add(dependent);
        }
    }

    public void remove(T key) {
        data.remove(key);
        for (var val : data.values()) {
            val.remove(key);
        }
    }

    private boolean hasSingleLoops() {
        for (T vertex : data.keySet()) {
            if (isDependencyLooped(vertex)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDependencyLooped(T dependency) {
        return data.get(dependency).contains(dependency);
    }

    public List<List<T>> findStronglyConnectedComponents() {
        var algorithm = new TarjanStronglyConnectedComponentsAlgorithm<T>();
        return algorithm.findStronglyConnectedComponents(data);
    }

    public List<T> getDependencies(T dependent) {
        return new ArrayList<>(data.get(dependent));
    }

    public boolean hasCycles() {
        var algorithm = new TarjanStronglyConnectedComponentsAlgorithm<T>();
        var components = algorithm.findStronglyConnectedComponents(data);
        return hasSingleLoops() || components.stream().anyMatch(component -> component.size() != 1);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public List<T> toOrderedList() {
        TopologicalSortAlgorithm<T> topologicalSortAlgorithm = new TopologicalSortAlgorithm<>();
        return topologicalSortAlgorithm.getSortedList(data);
    }
}
