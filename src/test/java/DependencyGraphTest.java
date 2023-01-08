import graph.DependencyGraph;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DependencyGraphTest {

    @org.junit.jupiter.api.Test
    void testCycleDetectionOnGraphWithCycles() {
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
    void testCycleDetectionOnGraphWithCycles1() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(3, 11);
        graph.addDependency(11, 3);
        assertSame(true, graph.hasCycles());
    }
    @org.junit.jupiter.api.Test
    void testCycleDetectionOnGraphWithCycles2() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(3, 11);
        graph.addDependency(11, 14);
        graph.addDependency(11, 12);
        graph.addDependency(14, 12);
        graph.addDependency(12, 3);
        assertSame(true, graph.hasCycles());
    }
    @org.junit.jupiter.api.Test
    void testCycleDetectionOnGraphWithoutCycles() {
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
    void testSingleLoopDetection() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(11, 11);
        assertSame(true, graph.hasCycles());
    }
    @org.junit.jupiter.api.Test
    void testCycleDetectionOnGraphWithoutCycles1() {
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
    void testToOrderedList() {
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
    void testFindingStronglyConnectedComponentsOnGraphWithCycles() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(1, 0);
        graph.addDependency(0, 3);
        graph.addDependency(2, 1);
        graph.addDependency(3, 2);
        graph.addDependency(4, 2);
        graph.addDependency(4, 6);
        graph.addDependency(5, 4);
        graph.addDependency(6, 5);
        graph.addDependency(7, 6);
        var result = graph.findStronglyConnectedComponents();
        var answer = Arrays.asList(Arrays.asList(0, 1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7));
        assertSame(true, compareListsWithComponents(answer, result));
    }


    @org.junit.jupiter.api.Test
    void testFindingStronglyConnectedComponentsOnGraphWithoutCycles() {
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(1, 0);
        graph.addDependency(2, 0);
        graph.addDependency(3, 1);
        graph.addDependency(3, 2);
        var result = graph.findStronglyConnectedComponents();
        var answer = Arrays.asList(Arrays.asList(0), Arrays.asList(1), Arrays.asList(2), Arrays.asList(3));
        assertSame(true, compareListsWithComponents(answer, result));
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
    private <T> boolean isValidTopologicalSort(DependencyGraph<T> graph, List<T> sortedObjects) {
        for (T key : sortedObjects) {
            if (!graph.getDependencies(key).isEmpty()) {
                return false;
            }
            graph.remove(key);
        }

        return true;
    }

    /**
     * Проверяет корректность нахождения сильно связанных компонент.
     * @param answer
     * @param result
     * @return
     * @param <T>
     */
    private <T> boolean compareListsWithComponents(List<List<T>> answer, List<List<T>> result) {
        if (answer.size() != result.size()) {
            return false;
        }

        for (List<T> answerComponent : answer) {
            boolean hasFound = false;
            for (List<T> resultComponent : answer) {
                if (resultComponent.equals(answerComponent)) {
                    hasFound = true;
                    break;
                }
            }

            if (!hasFound) {
                return false;
            }
        }

        return true;
    }
}