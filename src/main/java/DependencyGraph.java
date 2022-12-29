import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

// TODO: Проверка на петли и циклы в графе
public class DependencyGraph<T> {
    HashMap<T, List<T>> data = new HashMap<>();

    void addDependency(T dependent, T dependency) {
        if (!data.containsKey(dependent)) {
            data.put(dependent, new ArrayList<>());
        }
        data.get(dependent).add(dependency);
    }

    void addDependencies(T dependent, Collection<T> dependencies) {
        if (!data.containsKey(dependent)) {
            data.put(dependent, new ArrayList<>());
        }
        data.get(dependent).addAll(dependencies);
    }

    private void dfs(HashMap<T, Boolean> used, T key) {
        used.put(key, true);
    }

    boolean hasCycles() {
        HashMap<T, Boolean> used = new HashMap<>();
        for (var key : data.keySet()) {
            used.put(key, false);
        }

        for (var key : data.keySet()) {
            if (!used.get(key)) {
                dfs(used, key);
            }
        }

        return false;
    }

    List<T> toOrderedList() throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }
}
