package asserts;

import graph.DependencyGraph;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

public class ListAssert<T> extends AbstractAssert<ListAssert<T>, List<T>> {
    protected ListAssert(List<T> tList) {
        super(tList, ListAssert.class);
    }

    /**
     * Проверяет корректность топологической сортировки последовательным удалением вершин из графа.
     *
     * @param dependencyGraph граф зависимостей.
     * @return Самого себя.
     */
    public ListAssert<T> isTopologicallySorted(DependencyGraph<T> dependencyGraph) {
        for (T key : actual) {
            if (!dependencyGraph.getDependencies(key).isEmpty()) {
                failWithMessage("Invalid topological sort");
            }
            dependencyGraph.remove(key);
        }

        return this;
    }


}
