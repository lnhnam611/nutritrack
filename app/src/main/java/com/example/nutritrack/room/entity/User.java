package com.example.nutritrack.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "secret_question")
    public String secretQuestion;

    @ColumnInfo(name = "secret_answer")
    public String secretAnswer;

    @ColumnInfo(name = "gender")
    public String gender;

    @ColumnInfo(name = "age")
    public int age;

    @ColumnInfo(name = "weight")
    public double weight;

    @ColumnInfo(name = "height")
    public double height;


    public User(String name, @NonNull String email, String password,
                String secretQuestion, String secretAnswer) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.secretQuestion = secretQuestion;
        this.secretAnswer = secretAnswer;
    }

    @Ignore
    public User(String name, @NonNull String email, String password,
                String secretQuestion, String secretAnswer,
                int age, double weight, double height) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.secretQuestion = secretQuestion;
        this.secretAnswer = secretAnswer;
        this.age = age;
        this.weight = weight;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}