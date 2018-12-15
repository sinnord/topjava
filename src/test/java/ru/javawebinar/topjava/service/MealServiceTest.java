package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void create() {
        Meal meal = new Meal(LocalDateTime.of(2016, 1,1, 10, 0), "Завтрак", 600);
        Meal created = service.create(meal, USER_ID);
        meal.setId(created.getId());
        assertMatch(service.getAll(USER_ID), meal, USER_MEAL_1, USER_MEAL_2, USER_MEAL_3, USER_MEAL_4, USER_MEAL_5, USER_MEAL_6);
    }

    @Test(expected = DataAccessException.class)
    public void duplicateUserDateTimeCreate() throws Exception {
        service.create(
                new Meal(LocalDateTime.of(2015, 5, 31, 10, 0), "Завтрак", 800), USER_ID);
    }

    @Test
    public void update() throws Exception {
        Meal updated = new Meal(USER_MEAL_1);
        updated.setDescription("Перекус");
        updated.setCalories(800);
        service.update(updated, USER_ID);
        assertMatch(service.get(USER_MEAL_1.getId(), USER_ID), updated);
    }


    @Test
    public void delete() throws Exception {
        service.delete(USER_MEAL_1.getId(), USER_ID);
        assertMatch(service.getAll(USER_ID), USER_MEAL_2, USER_MEAL_3, USER_MEAL_4, USER_MEAL_5, USER_MEAL_6);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotFound() throws Exception {
        service.delete(1, USER_ID);
    }

    @Test
    public void get() {
        Meal meal = service.get(USER_MEAL_1.getId(), USER_ID);
        assertMatch(meal, USER_MEAL_1);
    }


    @Test
    public void getBetweenDateTimes() throws Exception {
        List<Meal> meals = service.getBetweenDateTimes(
                LocalDateTime.of(2015, 5, 31, 10, 0),
                LocalDateTime.of(2015, 5, 31, 21, 0),
                USER_ID
        );
        assertMatch(meals, USER_MEAL_1, USER_MEAL_2, USER_MEAL_3);
    }

    @Test
    public void getAll() throws Exception {
        List<Meal> userMeals = service.getAll(USER_ID);
        assertMatch(userMeals, USER_MEALS);
    }

    @Test(expected = NotFoundException.class)
    public void getOtherUserMeal() throws Exception {
        service.get(START_SEQ + 2, UserTestData.ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getNotFound() throws Exception {
        service.get(1, USER_ID);
    }

}