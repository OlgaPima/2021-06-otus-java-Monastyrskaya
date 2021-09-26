package homework;

import ru.otus.reflection.ReflectionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Класс - запускалка тестов
 */
public class TestRunner {

    public static void run(String className) throws Exception {
        ArrayList<Method> beforeMethods = new ArrayList<>();
        ArrayList<Method> testMethods = new ArrayList<>();
        ArrayList<Method> afterMethods = new ArrayList<>();

        Class<?> classRef = Class.forName(className);

        //Выцепляем public-методы, помеченные аннотациями @Before, @After, @Test. Допущение: все методы - без аргументов
        Method[] publicMethods = classRef.getDeclaredMethods();
        for (int i = 0; i < publicMethods.length; i++) {
            Method method = publicMethods[i];
            if (method.getAnnotation(homework.annotations.Before.class) != null) {
                beforeMethods.add(method);
            }
            else if (method.getAnnotation(homework.annotations.Test.class) != null) {
                testMethods.add(method);
            }
            else if (method.getAnnotation(homework.annotations.After.class) != null) {
                afterMethods.add(method);
            }
        }

        //Выполнение
        int testPassed = 0; //число пройденных тестов
        int testFailed = 0; //число упавших тестов
        int failedOnBefore = 0; //число тестов, упавших при инициализации

        for (Method testMethod : testMethods) {
            Object classInstance = ReflectionHelper.instantiate(classRef);
            //Object classInstance = Class.forName(className).getConstructor().newInstance();

            System.out.println("---------- Метод " + testMethod.getName() + "() --------");
            if (callMethods(classInstance, beforeMethods)) {
                try {
                    ReflectionHelper.callMethod(classInstance, testMethod);
                    System.out.println("test ok");
                    testPassed++;
                }
                catch (RuntimeException e) {
                    System.out.println(e.getMessage() + " " + ((InvocationTargetException)e.getCause()).getTargetException().getMessage());
                    System.out.println("test failed");
                    testFailed++;
                }
                finally {
                    callMethods(classInstance, afterMethods);
                }
            }
            else failedOnBefore++;
        }
        System.out.println("---------------------------------------");
        System.out.println("Всего тестов: " + testMethods.size());
        System.out.println("Количество пройденных тестов: " + testPassed);
        System.out.println("Количество тестов, упавших при вызове: " + testFailed);
        System.out.println("Количество не вызванных тестов (упавших на методах @Before): " + failedOnBefore);
    }

    private static boolean callMethods(Object classInstance, ArrayList<Method> methodsList) {
        try {
            for (Method method : methodsList) {
                ReflectionHelper.callMethod(classInstance, method);
            }
            return true;
        }
        catch (RuntimeException e) {
            // Исключение в отдельном методе не должно прерывать весь процесс тестирования.
            // Выводим ошибку в консоль, возвращаем false и едем дальше:
            System.out.println(e.getMessage() + " " + ((InvocationTargetException)e.getCause()).getTargetException().getMessage());
            return false;
        }
    }
}
