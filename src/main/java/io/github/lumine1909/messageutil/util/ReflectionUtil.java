package io.github.lumine1909.messageutil.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtil {

    public static void copyFields(Object from, Object to) {

        Class<?> fromClass = from.getClass();
        Class<?> toClass = to.getClass();

        while (fromClass != null) {
            for (Field field : fromClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Field toField = getFieldInHierarchy(toClass, field.getName());
                    toField.setAccessible(true);
                    toField.set(to, field.get(from));
                } catch (Exception ignored) {
                }
            }
            fromClass = fromClass.getSuperclass();
        }
    }

    private static Field getFieldInHierarchy(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
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