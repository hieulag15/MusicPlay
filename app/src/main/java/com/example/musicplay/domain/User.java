package com.example.musicplay.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("id")
    private long id;

    @SerializedName("phone")
    private String phone;

    @SerializedName("first_name")
    private String first_name;

    @SerializedName("last_name")
    private String lastn_name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role;

    public User() {
    }

    public User(long id, String phone, String first_name, String lastn_name, String email, String password, String role) {
        this.id = id;
        this.phone = phone;
        this.first_name = first_name;
        this.lastn_name = lastn_name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(long id, String phone, String first_name, String lastn_name, String email, String password) {
        this.id = id;
        this.phone = phone;
        this.first_name = first_name;
        this.lastn_name = lastn_name;
        this.email = email;
        this.password = password;
    }

    public User(String phone, String first_name, String lastn_name, String email, String password) {
        this.phone = phone;
        this.first_name = first_name;
        this.lastn_name = lastn_name;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLastn_name() {
        return lastn_name;
    }

    public void setLastn_name(String lastn_name) {
        this.lastn_name = lastn_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}