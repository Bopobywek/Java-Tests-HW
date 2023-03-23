package asserts;

import java.util.List;

public class Assertions {
    public static <T> ListAssert<T> assertThat(List<T> list) {
        return new ListAssert<>(list);
    }
    public static <T> ComponentsAssert<T> assertThatList(List<List<T>> list) {
        return new ComponentsAssert<>(list);
    }
}
