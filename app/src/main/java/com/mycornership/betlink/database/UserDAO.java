package com.mycornership.betlink.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.mycornership.betlink.models.User;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User profile);

    @Query("SELECT * FROM user WHERE username = :user")
    User getProfile(String user);

}
