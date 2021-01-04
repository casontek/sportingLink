package com.mycornership.betlink.database;

import android.app.Application;
import android.os.AsyncTask;

import com.mycornership.betlink.models.User;

public class UserRepository {
    private UserDAO userDAO;

    public UserRepository(Application application) {
        //creates database instance
        StoreDB db = StoreDB.getInstance(application);
        userDAO = db.userDAO();
    }

    public User getProfile(String username){

        return userDAO.getProfile(username);
    }

    public void inserProfile(User user){
        new InsertProfileAsynTasck(userDAO).execute(user);
    }

    public static class InsertProfileAsynTasck extends AsyncTask<User, Void, Void>{
        private UserDAO dao;

        public InsertProfileAsynTasck(UserDAO dao1) {
            this.dao = dao1;
        }

        @Override
        protected Void doInBackground(User... users) {
            dao.insert(users[0]);
            return null;
        }
    }
}
