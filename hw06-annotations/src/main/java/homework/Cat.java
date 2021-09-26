package homework;


/**
 * Тестируемый класс. Давайте погоняем котов :) (сколько ж можно решать технические и бизнес-задачи...)
 */
public class Cat {
    /**
     * Цвет кота. Специально использован более широкий enum, чем нужно, чтобы кидать исключения в конструкторе.
     */
    private final NatureColors color;

    /**
     * Кличка кота
     */
    private final String name;

    /**
     * Уровень энергии (насколько кот сыт: зависит от поступления еды и расхода калорий)
     */
    private int energy;

    /**
     * Необходимое потребление калорий в течение дня
     */
    private final int dailyCalories = 1000;

    /**
     * Жировой запас (когда ест больше нормы)
     */
    private int fat;

    /**
     * Режим сжигания жира
     */
    private boolean fatBurning;

    /**
     * Конструктор нашего шикарного, наглого, пушистого кота
     * @param color Цвет
     * @param name Кличка
     */
    public Cat(NatureColors color, String name) throws Exception {
        if (color.equals(NatureColors.BLUE) || color.equals(NatureColors.GREEN)
                || color.equals(NatureColors.RED) || color.equals(NatureColors.YELLOW)) {
            throw new Exception(color.name() + ": не бывает кошек такого цвета!");
        }

        this.color = color;
        this.name = name;
    }

    /**
     * Накормить кота. Увеличивает запас энергии на величину съеденных калорий.
     * @param calories
     */
    public void eat(int calories) throws Exception {
        System.out.println("Выдали еду: " + calories + " ккал");
        if (calories <= 0) {
            throw new Exception("А где еда-то?");
        }
        if (calories > dailyCalories*1.2) {
            calories = dailyCalories;
            throw new Exception("В меня столько не влезет! Не могу больше есть");
        }
        energy += calories;

        if (energy > dailyCalories) {
            fat += energy - dailyCalories; //переел - все лишнее в жировой запас
            energy = dailyCalories;
        }
    }

    /**
     * Погоня за мышами. Расход энергии 25 калорий/км
     * @param kilometers Сколько километров нужно намотать
     */
    public void run(int kilometers) throws Exception {
        System.out.println(String.format("Побежали... Кросс %d км за мышью", kilometers));
        decreaseEnergy(kilometers * 25);
    }

    /**
     * Мяукнуть. Расход энергии - 2 калории
     * @throws Exception
     */
    public void voice() throws Exception {
        System.out.println("Мяяяуу!");
        decreaseEnergy(2);
    }

    public void sleep() throws Exception {
        System.out.println("Спааать...");
        double rnd = Math.random();
        if (rnd > 0.8) {
            throw new Exception("Ночные кошмары: Шарик пришел в гости!!!");
        }
    }

    /**
     * Расход энергии на деятельность
     * @param value Значение расхода
     */
    private void decreaseEnergy(int value) throws Exception {
        energy = energy - value;
        if (energy < 0) {
            //Берем энергию из запасов, если они есть:
            fatBurning = true;
            fat += energy;
            energy = 0;
            //Если и запасов нет - громко возмущаемся и требуем жраааать:
            if (fat <= 0)
                throw new Exception("Больше нет сил! Покормите меня СРОЧНО!!!");
        }
    }

    public NatureColors getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getEnergy() {
        return energy;
    }

    public int getFat() { return fat; }

    public int getDailyCalories() { return dailyCalories; }
}
