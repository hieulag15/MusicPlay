package com.example.musicplay.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavouriteMessage {
    @SerializedName("message")
    private String message;

    @SerializedName("favorites")
    private List<Favourite> favourites ;

    @SerializedName("favorite")
    private Favourite favourite ;

    public FavouriteMessage(String message, List<Favourite> favourites, Favourite favourite) {
        this.message = message;
        this.favourites = favourites;
        this.favourite = favourite;
    }

    public FavouriteMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Favourite> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<Favourite> favourites) {
        this.favourites = favourites;
    }

    public Favourite getFavourite() {
        return favourite;
    }

    public void setFavourite(Favourite favourite) {
        this.favourite = favourite;
    }
}
