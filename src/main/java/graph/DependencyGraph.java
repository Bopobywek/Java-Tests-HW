package graph;

import graph.algorithm.TarjanStronglyConnectedComponentsAlgorithm;
import graph.algorithm.TopologicalSortAlgorithm;

import java.util.*;

/**
 * Класс, реализующий граф зависимостей.
 *
 * @param <T> тип объектов, хранящихся в графе.
 */
public class DependencyGraph<T> {
    /**
     * Граф хранится в виде списка смежности.
     */
    private final Map<T, List<T>> data = new HashMap<>();

    /**
     * Добавляет вершину в граф.
     *
     * @param key значение вершины.
     */
    public void add(T key) {
        if (!data.containsKey(key)) {
            data.put(key, new ArrayList<>());
        }
    }

    /**
     * Добавляет зависимость в граф.
     *
     * @param dependent  зависимая вершина.
     * @param dependency зависимость вершины.
     */
    public void addDependency(T dependent, T dependency) {
        add(dependent);
        add(dependency);
        data.get(dependent).add(dependency);
    }

    /**
     * Добавляет зависимости в граф.
     *
     * @param dependent    зависимая вершина.
     * @param dependencies зависимости вершины.
     */
    public void addDependencies(T dependent, Collection<T> dependencies) {
        for (T dependency : dependencies) {
            addDependency(dependent, dependency);
        }

        if (dependencies.isEmpty()) {
            add(dependent);
        }
    }

    /**
     * Удаляет вершину из графа.
     *
     * @param key значение вершины, которую нужно удалить.
     */
    public void remove(T key) {
        data.remove(key);
        for (var val : data.values()) {
            val.remove(key);
        }
    }

    /**
     * Проверяет граф на наличие петель.
     *
     * @return {@code true}, если в графе есть петли, иначе {@code false}.
     */
    private boolean hasSingleLoops() {
        for (T vertex : data.keySet()) {
            if (isDependencyLooped(vertex)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Проверяет, есть ли петля у некоторой вершины (т.е. зависит ли вершина от себя же).
     *
     * @param dependency вершина, которую нужно проверить.
     * @return {@code true}, если вершина зависит от себя же, иначе {@code false}.
     */
    public boolean isDependencyLooped(T dependency) {
        return data.get(dependency).contains(dependency);
    }

    /**
     * Находит <a href="https://en.wikipedia.org/wiki/Strongly_connected_component">компоненты сильной свзяности<a/>
     *
     * @return компоненты сильной связности.
     */
    public List<List<T>> findStronglyConnectedComponents() {
        var algorithm = new TarjanStronglyConnectedComponentsAlgorithm<T>();
        return algorithm.findStronglyConnectedComponents(data);
    }

    /**
     * Возвращает зависимости для данной вершины.
     *
     * @param dependent вершины, для которой нужно вернуть её зависимости.
     * @return зависимости вершины.
     */
    public List<T> getDependencies(T dependent) {
        return new ArrayList<>(data.get(dependent));
    }

    /**
     * Проверяет граф на наличие петель и циклов.
     *
     * @return {@code true}, если граф содержит петли или циклы, иначе {@code false}.
     */
    public boolean hasCycles() {
        var algorithm = new TarjanStronglyConnectedComponentsAlgorithm<T>();
        var components = algorithm.findStronglyConnectedComponents(data);
        return hasSingleLoops() || components.stream().anyMatch(component -> component.size() != 1);
    }

    /**
     * Проверяет, пустой ли граф.
     *
     * @return {@code true}, если граф пустой, иначе {@code false}.
     */
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Возвращает вершины, отсортированные в топологическом порядке, т.е. если вершина А, зависит от вершины В, то
     * вершина А в возвращаемом списке имеет индекс больше, чем индекс вершины B.
     *
     * @return список вершин в топологическом порядке.
     */
    public List<T> toOrderedList() {
        TopologicalSortAlgorithm<T> topologicalSortAlgorithm = new TopologicalSortAlgorithm<>();
        return topologicalSortAlgorithm.getSortedList(data);
    }
}
