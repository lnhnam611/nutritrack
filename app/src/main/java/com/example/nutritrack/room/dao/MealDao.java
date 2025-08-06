package com.example.nutritrack.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nutritrack.room.entity.Meal;

import java.util.List;

@Dao
public interface MealDao {

    @Insert
    void insertMeal(Meal meal);

    @Update
    void updateMeal(Meal meal);

    @Delete
    void deleteMeal(Meal meal);

    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY date DESC")
    List<Meal> getMealsByUserId(int userId);

    @Query("SELECT * FROM meals WHERE userId = :userId AND date(date) = date(:targetDate)")
    List<Meal> getMealsForUserByDate(int userId, String targetDate);

    @Query("DELETE FROM meals WHERE userId = :userId")
    void deleteAllMealsForUser(int userId);
}
