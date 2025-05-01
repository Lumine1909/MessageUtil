package io.github.lumine1909.messageutil.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class ReflectionUtil {

    public static void copyFields(Object from, Object to) {
        for (Field fromField : from.getClass().getDeclaredFields()) {
            fromField.setAccessible(true);
            try {
                Field toField = to.getClass().getDeclaredField(fromField.getName());
                toField.setAccessible(true);
                toField.set(to, fromField.get(from));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class FieldAccessor {

        private final Field field;

        public FieldAccessor(Class<?> clazz, String fieldName) {
            try {
                this.field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Object obj) {
            try {
                return (T) field.get(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void set(Object obj, Object value) {
            try {
                field.set(obj, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
