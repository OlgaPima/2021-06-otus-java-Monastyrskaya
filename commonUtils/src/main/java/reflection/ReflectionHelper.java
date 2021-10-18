package reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


public class ReflectionHelper {
    private ReflectionHelper() {
    }

    public static Object getFieldValue(Object object, String name) {
        try {
            var field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setFieldValue(Object object, String name, Object value) {
        try {
            var field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object callMethod(Object object, String methodName, Object... args) {
        try {
            var method = object.getClass().getDeclaredMethod(methodName, toClasses(args));
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object callMethod(Object object, Method method, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T instantiate(Class<T> type, Object... args) {
        try {
            if (args.length == 0) {
                return type.getDeclaredConstructor().newInstance();
            } else {
                Class<?>[] classes = toClasses(args);
                return type.getDeclaredConstructor(classes).newInstance(args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?>[] toClasses(Object[] args) {
        return Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
    }

    /**
     * Возвращает строковое представление сигнатур методов, отмеченных заданной аннотацией
     * @param classInstance Экземпляр класса, в котором искать методы
     * @param annotation Ссылка на класс аннотации, по которой ищем методы
     * @param <T>
     * @return Список сигнатур в строковом представлении (например, "interface homework.ICalculator plus(class java.lang.Integer, class java.lang.Integer)")
     * @throws Exception
     */
    public static <T extends Annotation> Set<String> findMethodsAnnotatedBy(Object classInstance, Class<T> annotation,
                                                                            boolean isProxy)
    {
        return ReflectionHelper.findMethodsAnnotatedBy(classInstance.getClass(), annotation, isProxy);
    }

    /**
     * Возвращает строковое представление сигнатур методов, отмеченных заданной аннотацией
     * @param classRef Ссылка на класс, в котором искать методы
     * @param annotation Ссылка на класс аннотации, по которой ищем методы
     * @param <T>
     * @return Список сигнатур в строковом представлении (например, "interface homework.ICalculator plus(class java.lang.Integer, class java.lang.Integer)")
     * @throws Exception
     */
    public static <T extends Annotation> Set<String> findMethodsAnnotatedBy(Class<?> classRef, Class<T> annotation,
                                                                            boolean isProxy)
    {
        Set<String> result = new TreeSet<>();
        Method[] classMethods = classRef.getDeclaredMethods();
        for (Method method : classMethods) {
            if ((!isProxy || method.isBridge()) && ReflectionHelper.isMethodAnnotatedBy(method, annotation)) {
                result.add(ReflectionHelper.getMethodSignature(method));
            }
        }
        return result;
    }

    public static String getMethodSignature(Method method) {
        String methodDescription = method.getReturnType() + " " + method.getName() + "(";
        StringBuilder paramsDescription = new StringBuilder();
        for (Parameter p : method.getParameters()) {
            paramsDescription.append(p.getType().toString()).append(", ");
        }
        if (paramsDescription.length() > 2)
            paramsDescription = new StringBuilder(paramsDescription.substring(0, paramsDescription.length() - 2));
        return methodDescription + paramsDescription + ")";
    }

    // ----- Fields -----------------------
    public static <T extends Annotation> boolean isMethodAnnotatedBy(Method method, Class<T> annotation) {
        return method.isAnnotationPresent(annotation);
    }

    public static <T extends Annotation> List<Field> findFieldsAnnotatedBy(Class<?> classRef, Class<T> annotation) {
        List<Field> result = new ArrayList<>();
        Field[] classFields = classRef.getDeclaredFields();
        for (Field field : classFields) {
            if (ReflectionHelper.isFieldAnnotatedBy(field, annotation)) {
                result.add(field);
            }
        }
        return result;
    }

    public static <T extends Annotation> boolean isFieldAnnotatedBy(Field field, Class<T> annotation) {
        return field.isAnnotationPresent(annotation);
    }
}
