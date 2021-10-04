package homework.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class ReflectionHelper {
    private ReflectionHelper() {
    }

    /**
     * Возвращает строковое представление сигнатур методов, отмеченных заданной аннотацией
     * @param classInstance Полное имя класса, в котором искать методы
     * @param annotation Ссылка на класс аннотации, по которой ищем методы
     * @param <T>
     * @return Список сигнатур в строковом представлении (например, "interface homework.ICalculator plus(class java.lang.Integer, class java.lang.Integer)")
     * @throws Exception
     */
    public static <T extends Annotation> Set<String> findMethodsByAnnotation(Object classInstance, Class<T> annotation) //throws Exception
    {
        Set<String> result = new TreeSet<>();
        Method[] classMethods = classInstance.getClass().getDeclaredMethods(); //Class.forName(className).getDeclaredMethods();
        for (Method method : classMethods) {
            if (method.isBridge() && ReflectionHelper.isMethodAnnotatedBy(method, annotation)) {
                result.add(ReflectionHelper.getMethodSignature(method));
            }
        }
        return result;
    }

    public static <T extends Annotation> boolean isMethodAnnotatedBy(Method method, Class<T> annotation) {
        if (method.getAnnotation(annotation) != null)
            return true;
        else return false;
    }

    public static String getMethodSignature(Method method) {
        String methodDescription = method.getReturnType() + " " + method.getName() + "(";
        String paramsDescription = "";
        for (Parameter p : method.getParameters()) {
            paramsDescription += p.getType().toString() + ", ";
        }
        if (paramsDescription.length() > 2)
            paramsDescription = paramsDescription.substring(0, paramsDescription.length()-2);
        return methodDescription + paramsDescription + ")";
    }
}
