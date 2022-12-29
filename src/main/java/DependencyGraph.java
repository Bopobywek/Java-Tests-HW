import javax.naming.OperationNotSupportedException;
import java.util.*;

public class DependencyGraph<T> {
    /**
     * Для хранения графа используется хэш-таблица, в которой ключи -- вершины графа,
     * а соответствующим значением является список соседей вершины.
     */
    private final HashMap<T, List<T>> data = new HashMap<>();

    void addDependency(T dependent, T dependency) {
        if (!data.containsKey(dependent)) {
            data.put(dependent, new ArrayList<>());
        }

        data.get(dependent).add(dependency);

        if (!data.containsKey(dependency)) {
            data.put(dependency, new ArrayList<>());
        }
    }

    void addDependencies(T dependent, Collection<T> dependencies) {
        for (T dependency : dependencies) {
            addDependency(dependent, dependency);
        }
    }

    List<T> getDependencies(T dependent) {
        return data.get(dependent);
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

    boolean hasCycles() {
        HashMap<T, Integer> visited = new HashMap<>();
        for (var key : data.keySet()) {
            visited.put(key, 0);
        }

        for (var key : data.keySet()) {
            if (checkCycles(visited, key)) {
                return true;
            }
        }

        return false;
    }

    boolean checkCycles(HashMap<T, Integer> visited, T key) {
        visited.put(key, 1);
        for (var neighbour : data.get(key)) {
            if (visited.get(neighbour) == 0) {
                if (checkCycles(visited, neighbour)) return true;
            } else if (visited.get(neighbour) == 1) {
                return true;
            }
        }
        visited.put(key, 2);
        return false;
    }

    List<T> toOrderedList() {
        ArrayList<T> answer = new ArrayList<>();
        HashMap<T, Boolean> visited = new HashMap<>();
        for (var key : data.keySet()) {
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
