package com.example.musicplay.api;

import com.example.musicplay.domain.FavouriteMessage;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FavouriteApi {

    @FormUrlEncoded
    @POST("favourite/find")
    Call<FavouriteMessage> findFavourite(@Field("songId") Long songId, @Field("userId") Long userId);

    @FormUrlEncoded
    @POST("favourite/listByUser")
    Call<FavouriteMessage> listByUser(@Field("userId") Long userId);

    @FormUrlEncoded
    @POST("favourite/add")
    Call<FavouriteMessage> addFavourite(@Field("songId") Long songId, @Field("userId") Long userId);

    @FormUrlEncoded
    @POST("favourite/delete")
    Call<FavouriteMessage> deleteFavourite(@Field("songId") Long songId, @Field("userId") Long userId);
}
