package com.example.musicplay.api;

import com.example.musicplay.domain.User;
import com.example.musicplay.domain.UserMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {
    @FormUrlEncoded
    @POST("user/login")
    Call<UserMessage> login(@Field("phone") String phone, @Field("password") String password);


    @POST("user/register")
    Call<UserMessage> register(@Body User user);


    @POST(value = "user/all")
    Call<UserMessage> getAllUser();

    @PUT("user/update/{id}")
    Call<User> update(@Path("id") long id, @Body User user);

    @FormUrlEncoded
    @POST("user/delete")
    Call<UserMessage> deleteUser(@Field("id") Long id);
}
