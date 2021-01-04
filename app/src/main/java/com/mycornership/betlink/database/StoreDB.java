package com.mycornership.betlink.database;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mycornership.betlink.models.User;

@Database(entities ={User.class}, version = 1)
public abstract class StoreDB extends RoomDatabase {
    private static StoreDB instance;

    public static StoreDB getInstance(Application application){
        if(instance == null){
            instance = Room.databaseBuilder(application, StoreDB.class, "sportinglink")
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    public abstract UserDAO userDAO();
}
