package model.base;

import java.lang.reflect.Field;
import java.util.List;

public interface Model<T> {

    /**
     * copy value
     * @return copied
     */
    T copy();

    /**
     * to list in the model
     * @return object list
     */
    List<Object> toList();

    /**
     * copies fields from one instance of the row to this one
     * @param other another instance of the class
     */
    default void apply(T other) {
        for (Field declaredField : getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);
            try {
                declaredField.set(this, declaredField.get(other));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
