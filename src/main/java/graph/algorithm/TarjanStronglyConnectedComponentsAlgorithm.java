package graph.algorithm;

import java.util.*;

/**
 * Класс, реализующий алгоритм Тарьяна для поиска компонент сильной связности.
 * Материал был взят из следующих источников:
 * <a href="https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm">wikipedia<a/>,
 * <a href="https://github.com/mission-peace/interview/blob/master/src/com/interview/graph/TarjanStronglyConnectedComponent.java">github<a/>
 *
 * @param <T> тип значений-вершин в графе.
 */
public class TarjanStronglyConnectedComponentsAlgorithm<T> {
    /**
     * Храним индексы для каждой из вершин.
     */
    private Map<T, Integer> indexesOfVertexes = new HashMap<>();
    /**
     * Для каждой вершины v храним наименьший индекс любой другой вершины на стеке, до которой можно добраться из v
     * через обход в глубину (причем сама вершина v также учитывается).
     */
    private Map<T, Integer> lowlinkOfVertexes = new HashMap<>();
    /**
     * Для получения информации о том, есть ли вершина на стеке за O(1), дополнительно храним такие вершины в
     * хэш-таблице.
     */
    private Set<T> onStack = new HashSet<>();
    /**
     * Поддерживаем стек, на который будут попадать вершины в порядке обхода.
     */
    private Stack<T> stack = new Stack<>();
    /**
     * Компоненты сильной связности.
     */
    private List<List<T>> stronglyConnectedComponents = new ArrayList<>();
    /**
     * Счётчик обработанных вершин.
     */
    private int index = 0;
    /**
     * Представление графа в виде списка смежности.
     */
    private Map<T, List<T>> graphRepresentation;

    /**
     * Восстанавливает состояние алгоритма к исходному.
     */
    private void resetToInitialState() {
        indexesOfVertexes = new HashMap<>();
        lowlinkOfVertexes = new HashMap<>();
        onStack = new HashSet<>();
        stack = new Stack<>();
        stronglyConnectedComponents = new ArrayList<>();
        index = 0;
    }

    /**
     * Находит компоненты сильной свзяности в переданном графе.
     *
     * @param graphRepresentation граф, представленный в виде списка смежности.
     * @return список компонент сильной связности.
     */
    public List<List<T>> findStronglyConnectedComponents(Map<T, List<T>> graphRepresentation) {
        resetToInitialState();
        this.graphRepresentation = graphRepresentation;
        for (var vertex : graphRepresentation.keySet()) {
            if (!indexesOfVertexes.containsKey(vertex)) {
                runFind(vertex);
            }
        }

        return stronglyConnectedComponents;
    }

    /**
     * Запускает обход для поиска компонент сильной связности.
     *
     * @param vertex вершина, от которой запускается обход.
     */
    private void runFind(T vertex) {
        indexesOfVertexes.put(vertex, index);
        lowlinkOfVertexes.put(vertex, index);
        index += 1;
        stack.push(vertex);
        onStack.add(vertex);

        for (var neighbour : graphRepresentation.get(vertex)) {
            if (!indexesOfVertexes.containsKey(neighbour)) {
                runFind(neighbour);
                int newLowlink = Math.min(lowlinkOfVertexes.get(vertex), lowlinkOfVertexes.get(neighbour));
                lowlinkOfVertexes.put(vertex, newLowlink);
            } else if (onStack.contains(neighbour)) {
                int newLowlink = Math.min(lowlinkOfVertexes.get(vertex), indexesOfVertexes.get(neighbour));
                lowlinkOfVertexes.put(vertex, newLowlink);
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
            stronglyConnectedComponents.add(component);
        }

    }
}
