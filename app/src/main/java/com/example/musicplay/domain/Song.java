package com.example.musicplay.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Song implements Serializable {
    @SerializedName("id")
    private long id;

    @SerializedName("name")
    private String name;

    @SerializedName("author")
    private String author;

    @SerializedName("singer")
    private String singer;

    @SerializedName("link")
    private String link;

    @SerializedName("image")
    private String image;

    @SerializedName("category")
    private Category category;

    public Song() {
    }

    public Song(long id, String name, String author, String singer, String link, String image, Category category) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.singer = singer;
        this.link = link;
        this.image = image;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
