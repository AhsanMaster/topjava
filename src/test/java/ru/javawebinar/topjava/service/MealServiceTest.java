package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "utf-8"))
public class MealServiceTest {
    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(ID_MEAL_1, USER_ID);
        assertMatch(meal, MEAL_1);
    }

    @Test
    public void delete() {
        service.delete(ID_MEAL_7, ADMIN_ID);
        assertMatch(service.getAll(ADMIN_ID), MEAL_8);
    }

    @Test
    public void getBetweenDates() {
        List<Meal> meals = service.getBetweenDates(MEAL_1.getDate(), MEAL_3.getDate(), USER_ID);
        assertMatch(meals, MEAL_3, MEAL_2, MEAL_1);
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(USER_ID);
        assertMatch(meals, USER_MEALS);
    }

    @Test
    public void update() {
        Meal updated = new Meal(MEAL_1);
        updated.setDescription("updated");
        updated.setCalories(777);
        service.update(updated, USER_ID);
        assertMatch(service.get(ID_MEAL_1, USER_ID), updated);
    }

    @Test
    public void create() {
        Meal newMeal = new Meal(LocalDateTime.of(2019, Month.OCTOBER, 22, 10, 0), "тест еда", 500);
        Meal createdMeal = service.create(newMeal, ADMIN_ID);
        newMeal.setId(createdMeal.getId());
        assertMatch(service.getAll(ADMIN_ID), newMeal, MEAL_8, MEAL_7);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() {
        service.get(ID_MEAL_1, ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() {
        service.delete(ID_MEAL_1, ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotFound() {
        service.update(MEAL_1, ADMIN_ID);
    }
}