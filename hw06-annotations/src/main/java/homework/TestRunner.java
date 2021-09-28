package homework;

import ru.otus.reflection.ReflectionHelper;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс - запускалка тестов
 */
public class TestRunner {
    
    public static void run(String className) throws Exception {
        Class<?> classRef = Class.forName(className);

        List<Method> beforeMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();

        findTestMethods(classRef, beforeMethods, testMethods, afterMethods);

        int testPassed = 0; //число пройденных тестов
        int testFailed = 0; //число упавших тестов
        int failedOnBefore = 0; //число тестов, упавших при инициализации

        for (Method testMethod : testMethods) {
            TestExecutionResult result = executeTests(classRef, beforeMethods, testMethod, afterMethods);
            switch (result) {
                case PASSED -> testPassed++;
                case FAILED -> testFailed++;
                case FAILED_ON_BEFORE -> failedOnBefore++;
            }
        }

        printResult(testMethods.size(), testPassed, testFailed, failedOnBefore);
    }

    private static void findTestMethods(Class<?> classRef, List<Method> beforeMethods,
                                        List<Method> testMethods, List<Method> afterMethods) {
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

    /**
     * Выполнение одного теста
     * @param classRef Ссылка на класс с тестами (на его экземпляре будет вызван тестовый мтеод)
     * @param beforeMethods Список before-методов
     * @param testMethod Вызываемый тестовый метод
     * @param afterMethods Список after-методов
     * @return
     */
    private static TestExecutionResult executeTests(Class<?> classRef, List<Method> beforeMethods,
                                                    Method testMethod, List<Method> afterMethods) {
        TestExecutionResult result;
        Object classInstance = ReflectionHelper.instantiate(classRef);

        System.out.println("---------- Метод " + testMethod.getName() + "() --------");
        if (callMethods(classInstance, beforeMethods)) {
            try {
                ReflectionHelper.callMethod(classInstance, testMethod);
                System.out.println("test ok");
                result = TestExecutionResult.PASSED;
            }
            catch (RuntimeException e) {
                System.out.println(getErrorMessage(e));
                System.out.println("test failed");
                result = TestExecutionResult.FAILED;
            }
        }
        else result = TestExecutionResult.FAILED_ON_BEFORE;
        callMethods(classInstance, afterMethods);
        return result;
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

    private static void printResult(int testCount, int passed, int failed, int failedOnBefore) {
        System.out.println("---------------------------------------");
        System.out.println("Всего тестов: " + testCount);
        System.out.println("Количество пройденных тестов: " + passed);
        System.out.println("Количество тестов, упавших при вызове: " + failed);
        System.out.println("Количество не вызванных тестов (упавших на методах @Before): " + failedOnBefore);
    }

    private static String getErrorMessage(RuntimeException e) {
        if (e.getCause() instanceof InvocationTargetException)
            return e.getMessage() + " " + ((InvocationTargetException)e.getCause()).getTargetException().getMessage();
        else
            return e.getMessage();
    }
}
