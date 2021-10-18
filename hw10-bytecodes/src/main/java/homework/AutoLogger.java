package homework;

import reflection.ReflectionHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Set;

public class AutoLogger {

    private AutoLogger() {
    }

    public static ICalculator createCalculator() {
        InvocationHandler handler = new DemoInvocationHandler(new Calculator());
        return (ICalculator) Proxy.newProxyInstance(AutoLogger.class.getClassLoader(),
                new Class<?>[]{ICalculator.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler
    {
        private final ICalculator calculator;
        private final Set<String> loggedMethods;

        public DemoInvocationHandler(Calculator calc) {
            this.calculator = calc;
            // Определяем в исходном классе методы, помеченные аннотацией @Log
            // (делаем здесь, т.к. в invoke() вызывается метод-обертка, и аннотации исходного метода уже недоступны).
            // Методы представляются в виде строковых сигнатур, затем в методе invoke() сопоставляются с сигнатурой вызванного метода -
            // таким образом определяется, нужно ли вызывать автологирование или нет.
            this.loggedMethods = ReflectionHelper.findMethodsAnnotatedBy(calc, homework.Log.class, true);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (loggedMethods.contains(ReflectionHelper.getMethodSignature(method))) {
                System.out.println("Logger, invoking method:" + method + ", params: " + Arrays.stream(args));
            }
            return method.invoke(calculator, args);
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" +
                    "calc=" + calculator +
                    '}';
        }
    }
}
