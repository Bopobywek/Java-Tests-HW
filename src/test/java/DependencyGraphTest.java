import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DependencyGraphTest {

    @org.junit.jupiter.api.Test
    void hasCycles() {
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
    void withoutCycles() {
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
        assertArrayEquals(result.toArray(), new Integer[] {7, 5, 11, 3, 8, 2, 9, 10});
    }
//
//    <T> boolean validateTopologicalSort(DependencyGraph<T> graph, List<T> sortedObjects) {
//        for (int i = sortedObjects.size() - 1; i >= 0; --i) {
//            sortedObjects.get(i);
//        }
//    }
}