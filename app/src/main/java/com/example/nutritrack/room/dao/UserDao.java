package com.example.nutritrack.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nutritrack.room.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();
}
