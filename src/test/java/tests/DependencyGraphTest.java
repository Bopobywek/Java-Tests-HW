package tests;

import graph.DependencyGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static asserts.Assertions.*;

class DependencyGraphTest {

    @ParameterizedTest
    @MethodSource("dependenciesWithCyclesProvider")
    void hasCycles_WhenCycleExist_ShouldReturnTrue(HashMap<Integer, List<Integer>> dependencies) {
        // Arrange
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        for (var dependency : dependencies.entrySet()) {
            graph.addDependencies(dependency.getKey(), dependency.getValue());
        }

        // Act, Assert
        assertTrue(graph.hasCycles());
    }

    @ParameterizedTest
    @MethodSource("dependenciesWithoutCyclesProvider")
    void hasCycles_WhenNoCycles_ShouldReturnFalse(HashMap<Integer, List<Integer>> dependencies) {
        // Arrange
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        for (var dependency : dependencies.entrySet()) {
            graph.addDependencies(dependency.getKey(), dependency.getValue());
        }

        // Act, Assert
        assertFalse(graph.hasCycles());
    }

    @Test
    void toOrderedList_ShouldSuccess() {
        // Arrange
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

        // Act
        var result = graph.toOrderedList();

        // Assert
        assertThat(result).isTopologicallySorted(graph);
    }

    @Test
    void findStronglyConnectedComponents_WhenGraphHasCycles_ShouldFindAll() {
        // Arrange
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
        var expected = Arrays.asList(Arrays.asList(0, 1, 2, 3), Arrays.asList(4, 5, 6), List.of(7));

        // Act
        var result = graph.findStronglyConnectedComponents();

        // Assert
        assertThatList(result).ContainsSameComponentsWith(expected);
    }

    @Test
    void findStronglyConnectedComponents_WhenGraphHasNoCycles_ShouldFindAll() {
        // Arrange
        DependencyGraph<Integer> graph = new DependencyGraph<>();
        graph.addDependency(1, 0);
        graph.addDependency(2, 0);
        graph.addDependency(3, 1);
        graph.addDependency(3, 2);
        var expected = Arrays.asList(List.of(0), List.of(1), List.of(2), List.of(3));

        // Act
        var result = graph.findStronglyConnectedComponents();

        // Assert
        assertThatList(result).ContainsSameComponentsWith(expected);
    }

    static Stream<HashMap<Integer, List<Integer>>> dependenciesWithCyclesProvider() {
        return Stream.of(
                new HashMap<>() {{
                    put(11, List.of(7, 5));
                    put(8, List.of(3, 7));
                    put(2, List.of(11));
                    put(9, List.of(11, 8));
                    put(10, List.of(11, 3));
                    put(3, List.of(10));
                }},
                new HashMap<>() {{
                    put(11, List.of(3));
                    put(3, List.of(11));
                }},
                new HashMap<>() {{
                    put(3, List.of(11));
                    put(11, List.of(14, 12));
                    put(14, List.of(12));
                    put(12, List.of(3));
                }},
                new HashMap<>() {{
                    put(11, List.of(11));
                }}
        );
    }

    static Stream<HashMap<Integer, List<Integer>>> dependenciesWithoutCyclesProvider() {
        return Stream.of(
                new HashMap<>() {{
                    put(11, List.of(7, 5));
                    put(8, List.of(3, 7));
                    put(2, List.of(11));
                    put(9, List.of(11, 8));
                    put(10, List.of(11, 3));
                }},
                /*
                0 --> 1
                |     |
                ∨     ∨
                2 --> 3
                */
                new HashMap<>() {{
                    put(1, List.of(0));
                    put(2, List.of(0));
                    put(3, List.of(1, 2));
                }}
        );
    }
}