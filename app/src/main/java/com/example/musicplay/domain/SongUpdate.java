package com.example.musicplay.domain;

public class SongUpdate {

    private String name;

    private String author;

    private String singer;

    private Category category;

    public SongUpdate() {
    }

    public SongUpdate(String name, String author, String singer, Category category) {
        this.name = name;
        this.author = author;
        this.singer = singer;
        this.category = category;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
