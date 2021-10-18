package homework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Данный класс не относится к ДЗ, просто упражнения с JUnit
 */
public class CatJUnitTest {

    @Test
    @DisplayName("Еда в нормальных условиях (не переедаем)")
    public void eat() throws Exception {
        Cat cat = new Cat(NatureColors.GRAY, "Murka");
        cat.eat(500);
        cat.eat(200);
        assertThat(cat.getEnergy()).isEqualTo(700);
    }

    @Test
    @DisplayName("Формирование жирового запаса")
    public void generateFat() throws Exception {
        Cat cat = new Cat(NatureColors.GRAY, "Vasily");
        cat.eat(1000);
        cat.eat(600);
        assertThat(cat.getFat()).isEqualTo(1600 - cat.getDailyCalories());
    }

    @Test
    @DisplayName("Тест на обжорство: съесть 2000 ккал")
    public void eatTooMuch() throws Exception {
        Cat cat = new Cat(NatureColors.GRAY, "Murka");
        assertThrows(Exception.class, () -> cat.eat(2000) );
    }

    @Test
    @DisplayName("Нельзя долго бегать без еды (начальный запас хода 25 км)")
    public void runTooLong() throws Exception {
        Cat cat = new Cat(NatureColors.ORANGE, "UseinBolt");
        cat.eat(500);
        assertThrows(Exception.class, () -> cat.run(30));
    }

    @Test
    @DisplayName("Поели - пошли песни петь. Тест голоса")
    public void voice() throws Exception {
        Cat cat = new Cat(NatureColors.WHITE, "Barsik");
        cat.eat(500);
        cat.voice();
        assertThat(cat.getEnergy()).isEqualTo(498);
    }

    @Test
    @DisplayName("На абсолютном нуле я даже мяукать не могу!")
    public void voiceHungry() throws Exception {
        Cat cat = new Cat(NatureColors.WHITE, "Murzik");
        assertThrows(Exception.class, () -> cat.voice());
    }

}