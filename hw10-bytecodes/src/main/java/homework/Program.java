package homework;

public class Program {
    public static void main(String[] args) {
        ICalculator calc = AutoLogger.createCalculator();
        System.out.println("---------------------");

        calc.calculate("20/5").plus(5).plus(1, 3); //Так не логируется полностью (проксирует только первый вызов из 3)
        //Так - проксирует нормально все 3 метода
        calc.calculate("20/5");
        calc.plus(5);
        calc.plus(1, 3);

        calc.plus(7);
        calc.plus(8);
        calc.minus(3);
        System.out.println("---------------------");
    }
}



