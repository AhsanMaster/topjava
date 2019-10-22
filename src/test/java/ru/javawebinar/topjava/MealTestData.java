package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int ID_MEAL_1 = START_SEQ + 2;
    public static final int ID_MEAL_2 = START_SEQ + 3;
    public static final int ID_MEAL_3 = START_SEQ + 4;
    public static final int ID_MEAL_4 = START_SEQ + 5;
    public static final int ID_MEAL_5 = START_SEQ + 6;
    public static final int ID_MEAL_6 = START_SEQ + 7;
    public static final int ID_MEAL_7 = START_SEQ + 8;
    public static final int ID_MEAL_8 = START_SEQ + 9;

    public static final Meal MEAL_1 = new Meal(ID_MEAL_1, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500);
    public static final Meal MEAL_2 = new Meal(ID_MEAL_2, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000);
    public static final Meal MEAL_3 = new Meal(ID_MEAL_3, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500);
    public static final Meal MEAL_4 = new Meal(ID_MEAL_4, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal MEAL_5 = new Meal(ID_MEAL_5, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500);
    public static final Meal MEAL_6 = new Meal(ID_MEAL_6, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510);
    public static final Meal MEAL_7 = new Meal(ID_MEAL_7, LocalDateTime.of(2015, Month.JUNE, 1, 14, 0), "Админ ланч", 510);
    public static final Meal MEAL_8 = new Meal(ID_MEAL_8, LocalDateTime.of(2015, Month.JUNE, 1, 21, 0), "Админ ужин", 1500);

    public static final Meal[] USER_MEALS = {MEAL_6, MEAL_5, MEAL_4, MEAL_3, MEAL_2, MEAL_1};

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingFieldByFieldElementComparator().isEqualTo(expected);
    }
}
