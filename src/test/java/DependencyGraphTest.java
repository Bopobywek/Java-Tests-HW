import util.DependencyGraph;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DependencyGraphTest {

    @org.junit.jupiter.api.Test
    void hasCycles1() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(11, 7);
        graph.addDependency(11, 5);
        graph.addDependency(8, 3);
        graph.addDependency(8, 7);
        graph.addDependency(2, 11);
        graph.addDependency(9, 11);
        graph.addDependency(10, 11);
        graph.addDependency(9, 8);
        graph.addDependency(10, 3);
        graph.addDependency(3, 10);
        assertSame(true, graph.hasCycles());
    }
    @org.junit.jupiter.api.Test
    void hasCycles2() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(3, 11);
        graph.addDependency(11, 3);
        assertSame(true, graph.hasCycles());
    }
    @org.junit.jupiter.api.Test
    void hasCycles3() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(3, 11);
        graph.addDependency(11, 14);
        graph.addDependency(11, 12);
        graph.addDependency(14, 12);
        graph.addDependency(12, 3);
        assertSame(true, graph.hasCycles());
    }
    @org.junit.jupiter.api.Test
    void withoutCycles1() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(11, 7);
        graph.addDependency(11, 5);
        graph.addDependency(8, 3);
        graph.addDependency(8, 7);
        graph.addDependency(2, 11);
        graph.addDependency(9, 11);
        graph.addDependency(10, 11);
        graph.addDependency(9, 8);
        graph.addDependency(10, 3);
        assertSame(false, graph.hasCycles());
    }
    @org.junit.jupiter.api.Test
    void withoutCycles2() {
        /*
        0 --> 1
        |     |
        ∨     ∨
        2 --> 3
         */
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(1, 0);
        graph.addDependency(2, 0);
        graph.addDependency(3, 1);
        graph.addDependency(3, 2);
        assertSame(false, graph.hasCycles());
    }

    @org.junit.jupiter.api.Test
    void toOrderedList() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(11, 7);
        graph.addDependency(11, 5);
        graph.addDependency(8, 3);
        graph.addDependency(8, 7);
        graph.addDependency(2, 11);
        graph.addDependency(9, 11);
        graph.addDependency(10, 11);
        graph.addDependency(9, 8);
        graph.addDependency(10, 3);
        var result = graph.toOrderedList();
        assertSame(true, isValidTopologicalSort(graph, result));
    }

    @org.junit.jupiter.api.Test
    void badSort() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(11, 7);
        graph.addDependency(11, 5);
        graph.addDependency(8, 3);
        graph.addDependency(8, 7);
        graph.addDependency(2, 11);
        graph.addDependency(9, 11);
        graph.addDependency(10, 11);
        graph.addDependency(9, 8);
        graph.addDependency(10, 3);
        var result = new Integer[] {10, 3, 9, 8, 11, 2, 7, 5};
        assertSame(false, isValidTopologicalSort(graph, Arrays.stream(result).toList()));
    }

    /**
     * Проверяет корректность топологической сортировки последовательным удалением вершин из графа.
     * @param graph граф зависимостей.
     * @param sortedObjects объекты, отсортированные в топологическом порядке.
     * @return {@code true}, если объекты отсортированы в топологическом порядке, иначе {@code false}
     * @param <T> тип объектов.
     */
    <T> boolean isValidTopologicalSort(DependencyGraph<T> graph, List<T> sortedObjects) {
        for (T key : sortedObjects) {
            if (!graph.getDependencies(key).isEmpty()) {
                return false;
            }
            graph.remove(key);
        }

        return true;
    }
}