package homework;

import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Класс для тестирования автоматического логирования
 */
public class Calculator implements ICalculator {

    private double result;

    /*
    Делаем у всех методов this в качестве возвращаемое значение, чтобы протестировать поведение прокси на Builder-подобных вызовах
    (например: calc.calculate("50/10").plus(4).minus(6)
     */

    @Log
    public Calculator calculate(String expression) {
        result = new ExpressionBuilder(expression).build().evaluate();
        printResult();
        return this;
    }

    @Log
    public Calculator calculate(Integer param1, char operator, Integer param2) {
        String op = String.valueOf(operator);
        result = new ExpressionBuilder(param1 + op + param2).build().evaluate();
        printResult();
        return this;
    }

    @Log
    public Calculator plus(Integer param1) {
        result += param1.doubleValue();
        printResult();
        return this;
    }

    @Log
    public Calculator plus(Integer param1, Integer param2) {
        result += param1.doubleValue() + param2.doubleValue();
        printResult();
        return this;
    }

    //Не логируем этот метод
    public Calculator minus(Integer param1) {
        result -= param1.doubleValue();
        printResult();
        return this;
    }

    //Не логируем этот метод
    public Calculator minus(Integer param1, Integer param2) {
        result -= param1.doubleValue() + param2.doubleValue();
        printResult();
        return this;
    }

    private void printResult() {
        System.out.println("Calculator: result=" + result);
    }
}
