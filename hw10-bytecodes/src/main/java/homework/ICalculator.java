package homework;

public interface ICalculator {

    ICalculator calculate(String expression);
    ICalculator calculate(Integer param1, char operator, Integer param2);
    ICalculator plus(Integer param1);
    ICalculator plus(Integer param1, Integer param2);
    ICalculator minus(Integer param1);
    ICalculator minus(Integer param1, Integer param2);
}
