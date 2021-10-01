package homework;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;


/**
 * Класс с тестами для запуска через Reflection и аннотации
 */
public class CatTest {

    private Cat cat;

    public CatTest() {}

    /**
     * Инициализация ресурсов для каждого теста
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        //Рандомно выбираем цвет кота. Далее конструктор может упасть, если кот неправильного цвета (желтый, зеленый, синий, красный).
        //Сделано специально, чтобы метод setUp() иногда падал.
        NatureColors color;
        double rndColor = Math.random();
        if (rndColor <= 0.125) {
            color = NatureColors.GREEN;
        }
        else if (rndColor <= 0.25) {
            color = NatureColors.YELLOW;
        }
        else if (rndColor <= 0.375) {
            color = NatureColors.RED;
        }
        else if (rndColor <= 0.5) {
            color = NatureColors.BLUE;
        }
        else if (rndColor <= 0.625) {
            color = NatureColors.BLACK;
        }
        else if (rndColor <= 0.75) {
            color = NatureColors.WHITE;
        }
        else if (rndColor <= 0.875) {
            color = NatureColors.GRAY;
        }
        else color = NatureColors.ORANGE;

        cat = new Cat(color, "Vasyl");
    }

    @Test  //ok
    public void eat() throws Exception {
        cat.eat(500);
        cat.eat(200);
        assertThatEquals(cat.getEnergy(), 700);
    }

    @Test //ok
    public void eatTooMuch() throws Exception {
        assertThrows(() -> cat.eat(2000) );
    }

    @Test  //тест должен упасть - не то сообщение об ошибке
    public void eatEmptyDinner() throws Exception {
        assertThrows(() -> cat.eat(0), "Миска пустая...");
    }

    @Test  //ok
    public void generateFat() throws Exception {
        cat.eat(1000);
        cat.eat(600);
        assertThatEquals(1600 - cat.getDailyCalories(), cat.getFat());
    }

    @Test //тест должен упасть - запаса хода не хватит
    public void longRunTestWithoutEat() throws Exception {
        cat.eat(400);
        cat.run(30);
    }

    @Test //ok
    public void normalRunTestWithEat() throws Exception {
        cat.eat(1000);
        cat.run(5);
        assertThatEquals(cat.getEnergy(), 875);
    }

    @Test //тест должен упасть - не учтены затраты энергии на мяв
    public void voice() throws Exception {
        cat.eat(500);
        cat.voice();
        assertThatEquals(cat.getEnergy(), 500);
    }

    @Test //ok - будет выброшено исключение (кот вообще не накормлен, энергия 0)
    public void voiceHungry() throws Exception {
        assertThrows(() -> cat.voice());
    }

    /**
     * Закрытие (освобождение) ресурсов после теста
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (cat != null)
            cat.sleep();
    }

    //Далее - попытка имитировать методы AssertJ
    private void assertThatEquals(int actualValue, int expectedValue) throws Exception {
        if (actualValue != expectedValue) {
            String testMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            throw new Exception(String.format("Метод " + testMethodName + "(): ожидаемое значение (%d) не совпадает с актуальным (%d)",
                    expectedValue, actualValue));
        }
    }

    private void assertThrows(Runnable runnable) throws Exception {
        assertThrows(runnable, null);
    }

    private void assertThrows(Runnable runnable, String message) throws Exception {
        String testMethodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        try {
            runnable.run();
            throw new Exception("Метод " + testMethodName + "(): не выброшено ожидаемое исключение");
        }
        catch (Throwable e) {
            if (message != null && !message.isEmpty() && !e.getMessage().equals(message))
                throw new Exception("Метод " + testMethodName + "(): актуальное сообщение об ошибке не соответствует ожидаемому\r\n" +
                        "       Актуальное сообщение: " + e.getMessage() + "\r\n" +
                        "       Ожидаемое сообщение:  " + message);
        }
    }
}
