package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {

    void deleteMeal(int id);

    Meal addMeal(Meal meal);

    Meal updateMeal(Meal meal);

    List<Meal> findAll();

    Meal find(int id);

}
