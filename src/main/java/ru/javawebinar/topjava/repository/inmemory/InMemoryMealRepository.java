package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
        if (meal.isNew()) {
            log.info("New meal {} by user {} ", meal.getId(), userId);
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            getMapUserMeals(userId).put(meal.getId(), meal);
            return meal;
        }
        // treat case: update, but not present in storage
        log.info("Updated meal {} by user {} ", meal.getId(), userId);
        return getMapUserMeals(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);

    }

    @Override
    public boolean delete(int id, int userId) {
        return getMapUserMeals(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        return getMapUserMeals(userId).get(id);
    }

    @Override
    public List<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return getMapUserMeals(userId).values()
                .stream()
                .filter(meal -> DateTimeUtil.isBetween(meal.getDate(), startDate, endDate))
                .filter(meal -> DateTimeUtil.isBetween(meal.getTime(), startTime, endTime))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getMapUserMeals(userId).values()
                .stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private Map<Integer, Meal> getMapUserMeals(int userId) {
        Map<Integer, Meal> resultMap = repository.putIfAbsent(userId, new HashMap<>());
        if (resultMap == null) {
            return repository.get(userId);
        } else {
            return resultMap;
        }
    }
}

