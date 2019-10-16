package ru.javawebinar.topjava.repository.inmemory;


import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = getLogger(InMemoryMealRepository.class);
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, 1));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> userMeals = getMapUserMeals(userId);
        if(userMeals == null){
            putMapUserMeals(userId);
            userMeals = getMapUserMeals(userId);
        }
        if (meal.isNew()) {
            log.info("New meal {} by user {} ", meal.getId(), userId);
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            userMeals.put(meal.getId(), meal);
            return meal;
        }
        // treat case: update, but not present in storage
        log.info("Updated meal {} by user {} ", meal.getId(), userId);
        return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);

    }

    @Override
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> userMeals = getMapUserMeals(userId);
        return userMeals == null ? false : userMeals.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Map<Integer, Meal> userMeals = getMapUserMeals(userId);
        return userMeals == null ? null : userMeals.get(id);
    }

    @Override
    public List<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        //можно еще без List. а сразу через predicate.and
        List<Predicate<Meal>> filters = new ArrayList<>();
        filters.add(meal -> DateTimeUtil.isBetween(meal.getDate(), startDate, endDate));
        filters.add(meal -> DateTimeUtil.isBetween(meal.getTime(), startTime, endTime));

        return getAllFiltered(userId, filters.stream().reduce(x -> true, Predicate::and));
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getAllFiltered(userId, meal -> true);
    }

    public List<Meal> getAllFiltered(int userId, Predicate<Meal> filter) {
        return getMapUserMeals(userId).values()
                .stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private Map<Integer, Meal> getMapUserMeals(int userId) { //можно было бы просто сразу через get, но вдруг структура поменяется
         return repository.get(userId);
    }
    private void putMapUserMeals(int userId){
        repository.putIfAbsent(userId, new HashMap<>());
    }
}

