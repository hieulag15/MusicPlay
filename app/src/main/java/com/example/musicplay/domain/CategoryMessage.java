package com.example.musicplay.domain;

import com.google.gson.annotations.SerializedName;

public class CategoryMessage {
    @SerializedName("message")
    private String message;
    @SerializedName("category")
    private Category category;

    public CategoryMessage(String message, Category category) {
        this.message = message;
        this.category = category;
    }
    public CategoryMessage() {
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
