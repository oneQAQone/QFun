package me.yxp.qfun.utils.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MethodUtils {

    public static Finder create(Class<?> clazz) {
        return new Finder(clazz);
    }

    public enum AccessModifier {
        ANY, PUBLIC, PROTECTED, PACKAGE_PRIVATE, PRIVATE
    }

    public static class Finder {
        private final Class<?> mClazz;
        private String mMethodName;
        private Class<?> mReturnType;
        private Class<?>[] mParamTypes;
        private Integer mParamCount;
        private AccessModifier mAccessModifierFilter = AccessModifier.ANY;

        public Finder(Class<?> clazz) {
            mClazz = clazz;
        }

        public Finder withMethodName(String methodName) {
            mMethodName = methodName;
            return this;
        }

        public Finder withReturnType(Class<?> returnType) {
            mReturnType = returnType;
            return this;
        }

        public Finder withParamTypes(Class<?>... paramTypes) {
            mParamTypes = paramTypes;
            mParamCount = (paramTypes != null) ? paramTypes.length : null;
            return this;
        }

        public Finder withParamCount(int paramCount) {
            mParamCount = paramCount;
            return this;
        }

        public Finder withAccessModifier(AccessModifier modifier) {
            mAccessModifierFilter = modifier;
            return this;
        }

        public Method findOne() throws Exception {
            List<Method> methods = findAll();
            if (methods.isEmpty()) {
                return null;
            }

            for (Method method : methods) {
                if (isExactMatch(method)) {
                    return makeAccessible(method);
                }
            }

            return makeAccessible(methods.get(0));
        }

        public List<Method> findAll() {
            List<Method> candidates = new ArrayList<>();

            for (Method method : mClazz.getDeclaredMethods()) {
                if (mAccessModifierFilter != AccessModifier.ANY) {
                    AccessModifier modifier = getAccessModifier(method);
                    if (modifier != mAccessModifierFilter) {
                        continue;
                    }
                }

                if (mMethodName != null && !method.getName().equals(mMethodName)) {
                    continue;
                }

                if (mReturnType != null && !mReturnType.equals(method.getReturnType())) {
                    continue;
                }

                Class<?>[] methodParams = method.getParameterTypes();

                if (mParamCount != null && methodParams.length != mParamCount) {
                    continue;
                }

                if (mParamTypes != null) {
                    if (methodParams.length != mParamTypes.length) {
                        continue;
                    }

                    boolean match = true;
                    for (int i = 0; i < mParamTypes.length; i++) {
                        if (mParamTypes[i] == null) continue;
                        if (!methodParams[i].isAssignableFrom(mParamTypes[i])) {
                            match = false;
                            break;
                        }
                    }
                    if (!match) {
                        continue;
                    }
                }

                candidates.add(method);
            }

            candidates.sort(Comparator.comparingInt(this::calculateMatchScore)
                    .reversed()
                    .thenComparing(Method::getName)
                    .thenComparing(Method::getParameterCount));

            if (!candidates.isEmpty()) {
                return candidates;
            } else {
                throw new RuntimeException("无符合方法");
            }
        }

        private Method makeAccessible(Method method) {
            if (!Modifier.isPublic(method.getModifiers())
                    || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                method.setAccessible(true);
            }
            return method;
        }

        private AccessModifier getAccessModifier(Method method) {
            int mod = method.getModifiers();
            if (Modifier.isPrivate(mod)) {
                return AccessModifier.PRIVATE;
            }
            if (Modifier.isProtected(mod)) {
                return AccessModifier.PROTECTED;
            }
            if (Modifier.isPublic(mod)) {
                return AccessModifier.PUBLIC;
            }
            return AccessModifier.PACKAGE_PRIVATE;
        }

        private boolean isExactMatch(Method method) {
            if (mParamTypes == null) {
                return false;
            }

            Class<?>[] methodParams = method.getParameterTypes();
            for (int i = 0; i < mParamTypes.length; i++) {
                if (mParamTypes[i] != methodParams[i]) {
                    return false;
                }
            }
            return true;
        }

        private int calculateMatchScore(Method method) {
            int score = 0;

            if (isExactMatch(method)) {
                score += 100;
            }

            if (mParamTypes != null) {
                Class<?>[] methodParams = method.getParameterTypes();
                for (int i = 0; i < mParamTypes.length; i++) {
                    if (mParamTypes[i] == methodParams[i]) {
                        score += 10;
                    } else if (methodParams[i].isAssignableFrom(mParamTypes[i])) {
                        score += calculateInheritanceDepth(mParamTypes[i], methodParams[i]);
                    }
                }
            }

            return score;
        }

        private int calculateInheritanceDepth(Class<?> child, Class<?> parent) {
            int depth = 0;
            Class<?> current = child;

            while (current != null && current != parent) {
                depth++;
                current = current.getSuperclass();
            }

            return (current == parent) ? depth : -1;
        }
    }
}
