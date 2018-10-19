package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class MealRepositoryInMemory implements MealRepository {

    public static List<Meal> mealList = new ArrayList<>();

    static {
        mealList.add(new Meal(1, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500));
        mealList.add(new Meal(2, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000));
        mealList.add(new Meal(3, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500));
        mealList.add(new Meal(4, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000));
        mealList.add(new Meal(5, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500));
        mealList.add(new Meal(6, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510));
    }

    public void delete(int id) {
        mealList = mealList.stream()
                .filter(meal -> meal.getId() != id)
                .collect(toList());
    }

    public Meal save(Meal meal) {
        if (meal.getId() == 0) {
            int maxId = mealList.stream()
                    .mapToInt(Meal::getId)
                    .max().orElse(0);
            meal.setId(maxId + 1);
            mealList.add(meal);
        } else {
            mealList = mealList.stream()
                    .filter(mealfrom -> mealfrom.getId() != meal.getId())
                    .collect(toList());
            mealList.add(meal);
        }
        return meal;
    }

    public List<Meal> getAll() {
        return mealList;
    }

    public Meal get(int id) {
        return mealList.stream()
                .filter(meal -> id == meal.getId())
                .findFirst().orElse(null);
    }
}
