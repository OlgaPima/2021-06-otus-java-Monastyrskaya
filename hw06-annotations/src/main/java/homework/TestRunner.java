package homework;

import ru.otus.reflection.ReflectionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс - запускалка тестов
 */
public class TestRunner {

    private static Class<?> classRef;

    private static List<Method> beforeMethods;
    private static List<Method> testMethods;
    private static List<Method> afterMethods;

    private static int testPassed = 0; //число пройденных тестов
    private static int testFailed = 0; //число упавших тестов
    private static int failedOnBefore = 0; //число тестов, упавших при инициализации

    public static void run(String className) throws Exception {
        classRef = Class.forName(className);

        findTestMethods();
        executeTests();
        printResult();
    }

    private static void findTestMethods() {
        beforeMethods = new ArrayList<>();
        testMethods = new ArrayList<>();
        afterMethods = new ArrayList<>();

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
    }

    private static void executeTests() {
        //Выполнение
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
                    System.out.println(getErrorMessage(e));
                    System.out.println("test failed");
                    testFailed++;
                }
            }
            else failedOnBefore++;
            callMethods(classInstance, afterMethods);
        }
    }

    private static boolean callMethods(Object classInstance, List<Method> methodsList) {
        try {
            for (Method method : methodsList) {
                ReflectionHelper.callMethod(classInstance, method);
            }
            return true;
        }
        catch (RuntimeException e) {
            // Исключение в отдельном методе не должно прерывать весь процесс тестирования.
            // Выводим ошибку в консоль, возвращаем false и едем дальше:
            System.out.println(getErrorMessage(e));
            return false;
        }
    }

    private static void printResult() {
        System.out.println("---------------------------------------");
        System.out.println("Всего тестов: " + testMethods.size());
        System.out.println("Количество пройденных тестов: " + testPassed);
        System.out.println("Количество тестов, упавших при вызове: " + testFailed);
        System.out.println("Количество не вызванных тестов (упавших на методах @Before): " + failedOnBefore);
    }

    private static String getErrorMessage(RuntimeException e) {
        if (e.getCause() instanceof InvocationTargetException)
            return e.getMessage() + " " + ((InvocationTargetException)e.getCause()).getTargetException().getMessage();
        else
            return e.getMessage();
    }
}
