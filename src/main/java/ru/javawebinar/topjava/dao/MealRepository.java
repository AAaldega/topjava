package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;

public interface MealRepository {

    void delete(int id);

    Meal save(Meal meal);

    Collection<Meal> getAll();

    Meal get(int id);

}
