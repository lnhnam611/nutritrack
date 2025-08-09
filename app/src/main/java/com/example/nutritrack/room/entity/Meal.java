package com.example.nutritrack.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.util.Date;

@Entity(
        tableName = "meals",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class Meal {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userId")
    public int userId;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "food_name")
    public String foodName;

    @ColumnInfo(name = "total_calories")
    public double totalCalories;

    public Meal(int userId, Date date, String foodName, double totalCalories) {
        this.userId = userId;
        this.date = date;
        this.foodName = foodName;
        this.totalCalories = totalCalories;
    }

    @Ignore
    public Meal(){}


}

