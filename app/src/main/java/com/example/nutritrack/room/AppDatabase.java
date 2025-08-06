package com.example.nutritrack.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.nutritrack.room.dao.MealDao;
import com.example.nutritrack.room.dao.UserDao;
import com.example.nutritrack.room.entity.Meal;
import com.example.nutritrack.room.entity.User;


@Database(entities = {User.class, Meal.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract MealDao mealDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "nutri_db"
                            )
                            .fallbackToDestructiveMigration() // Wipes and rebuilds DB on schema change
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
