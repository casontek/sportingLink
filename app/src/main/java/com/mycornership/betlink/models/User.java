package com.mycornership.betlink.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "user")
public class User {
    @SerializedName("id")
    private long id;
    @NonNull
    @PrimaryKey
    @SerializedName("username")
    private String username;
    @SerializedName("phone")
    private String phone;
    @SerializedName("photo")
    private String photo;
    @SerializedName("level")
    private int level;
    @SerializedName("role")
    private String role;
    @SerializedName("country")
    private String country;
    @SerializedName("joinDate")
    private String joined;
    @SerializedName("totalPost")
    private int post;
    @SerializedName("totalWin")
    private int won;
    @SerializedName("wallet")
    private String wallet;
    @SerializedName("email")
    private String email;

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getJoined() {
        return joined;
    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", photo='" + photo + '\'' +
                ", level=" + level +
                ", role='" + role + '\'' +
                ", country='" + country + '\'' +
                ", joined='" + joined + '\'' +
                ", post=" + post +
                ", won=" + won +
                ", wallet='" + wallet + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
