package com.mycornership.betlink.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.mycornership.betlink.models.User;

public class UserVModel extends AndroidViewModel {
    private UserRepository repository;

    public UserVModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public void add(User user){

        repository.inserProfile(user);
    }

    public User get(String username){

        return repository.getProfile(username);
    }

}
