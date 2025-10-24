package me.yxp.qfun.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FieldUtils {

    public static Finder create(Object target) {
        return new Finder(target);
    }

    public static class Finder {
        private final Object mTarget;
        private String mFieldName;
        private Class<?> mFieldType;
        private Class<?> mParentClass;

        public Finder(Object target) {
            mTarget = target;
        }

        public Finder withName(String fieldName) {
            mFieldName = fieldName;
            return this;
        }

        public Finder ofType(Class<?> fieldType) {
            mFieldType = fieldType;
            return this;
        }

        public Finder inParent(Class<?> parentClass) {
            mParentClass = parentClass;
            return this;
        }

        public Object getValue() {
            Field field = findField(mTarget.getClass());
            if (field == null) {
                return null;
            }

            makeAccessible(field);
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    return field.get(null);
                } else {
                    return field.get(mTarget);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to get field value", e);
            }
        }

        public void setValue(Object value) {
            Field field = findField(mTarget.getClass());
            if (field == null) {
                return;
            }

            makeAccessible(field);
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    field.set(null, value);
                } else {
                    field.set(mTarget, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set field value", e);
            }
        }

        private Field findField(Class<?> targetClass) {
            if (mFieldName == null && mFieldType == null) {
                throw new IllegalArgumentException("At least one search condition (name or type) must be specified");
            }

            Field field = null;

            if (mParentClass != null && mParentClass.isAssignableFrom(targetClass)) {
                field = findInClass(mParentClass);
            }

            if (field == null) {
                field = findInClass(targetClass);
            }

            return field;
        }

        private Field findInClass(Class<?> clazz) {
            try {
                if (mFieldName != null) {

                    Field field = clazz.getDeclaredField(mFieldName);

                    if (mFieldType != null && !mFieldType.isAssignableFrom(field.getType())) {
                        return null;
                    }

                    return field;
                } else {
                    // 按类型查找：优先返回非静态字段
                    List<Field> instanceFields = new ArrayList<>();
                    List<Field> staticFields = new ArrayList<>();

                    // 首先收集所有符合条件的字段并按静态/实例分类
                    for (Field field : clazz.getDeclaredFields()) {
                        if (mFieldType.isAssignableFrom(field.getType())) {
                            if (Modifier.isStatic(field.getModifiers())) {
                                staticFields.add(field);
                            } else {
                                instanceFields.add(field);
                            }
                        }
                    }

                    // 优先返回实例字段（非静态）
                    if (!instanceFields.isEmpty()) {
                        return instanceFields.get(0); // 返回第一个实例字段
                    }

                    // 如果没有实例字段，则返回静态字段
                    if (!staticFields.isEmpty()) {
                        return staticFields.get(0); // 返回第一个静态字段
                    }
                }
            } catch (NoSuchFieldException e) {
                // 按名称查找时未找到字段
            }

            return null;
        }

        private void makeAccessible(Field field) {
            if (!Modifier.isPublic(field.getModifiers())
                    || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
                field.setAccessible(true);
            }
        }
    }
}