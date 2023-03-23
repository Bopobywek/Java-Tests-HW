package asserts;

import org.assertj.core.api.AbstractAssert;

import java.util.List;

public class ComponentsAssert<T> extends AbstractAssert<ComponentsAssert<T>, List<List<T>>> {
    protected ComponentsAssert(List<List<T>> lists) {
        super(lists, ComponentsAssert.class);
    }

    /**
     * Проверяет корректность нахождения сильно связанных компонент.
     *
     * @param expected список-ответ.
     * @return Самого себя.
     */
    public ComponentsAssert<T> ContainsSameComponentsWith(List<List<T>> expected) {
        if (expected.size() != actual.size()) {
            failWithMessage("Invalid ");
        }

        for (List<T> answerComponent : expected) {
            boolean hasFound = false;
            for (List<T> resultComponent : expected) {
                if (resultComponent.equals(answerComponent)) {
                    hasFound = true;
                    break;
                }
            }

            if (!hasFound) {
                failWithMessage("Invalid ");
            }
        }

        return this;
    }
}
