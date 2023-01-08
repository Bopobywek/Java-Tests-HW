package util;

import java.util.*;

public class DependencyGraph<T> {
    /**
     * Для хранения графа используется хэш-таблица, в которой ключи -- вершины графа,
     * а соответствующим значением является список соседей вершины.
     */
    private final HashMap<T, List<T>> data = new HashMap<>();

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

    public List<T> getDependencies(T dependent) {
        return new ArrayList<>(data.get(dependent));
    }

    private void dfs(HashMap<T, Boolean> visited, ArrayList<T> answer, T key) {
        visited.put(key, true);
        for (T neighbour : data.get(key)) {
            if (!visited.get(neighbour)) {
                dfs(visited, answer, neighbour);
            }
        }

        answer.add(key);
    }

    public boolean hasCycles() {
        HashMap<T, Integer> visited = new HashMap<>();
        for (var key : data.keySet()) {
            visited.put(key, 0);
        }

        for (var key : data.keySet()) {
            if (visited.get(key) == 0 && trySort(visited, null, key)) {
                return true;
            }
        }

        return false;
    }

    private boolean trySort(HashMap<T, Integer> visited, ArrayList<T> answer, T key) {
        visited.put(key, 1);
        for (var neighbour : data.get(key)) {
            if (visited.get(neighbour) == 0) {
                if (trySort(visited, answer, neighbour)) return true;
            } else if (visited.get(neighbour) == 1) {
                return true;
            }
        }
        visited.put(key, 2);
        if (answer != null) {
            answer.add(key);
        }
        return false;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public List<T> toOrderedList() {
        ArrayList<T> answer = new ArrayList<>();
        HashMap<T, Boolean> visited = new HashMap<>();
        for (var key : data.keySet().stream().sorted().toList()) {
            visited.put(key, false);
        }

        for (var key : data.keySet()) {
            if (!visited.get(key)) {
                dfs(visited, answer, key);
            }
        }

        return answer;
    }
}
