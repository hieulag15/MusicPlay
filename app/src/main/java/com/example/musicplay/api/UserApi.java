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

    @POST("user/all")
    Call<UserMessage> getAllUser();

    @FormUrlEncoded
    @POST("user/delete")
    Call<UserMessage> deteleUser(@Field("userId") Long id);

    @PUT("user/update")
    Call<UserMessage> updateUser(@Path("id") Long id, @Body User user);

    @POST("user/register")
    Call<UserMessage> register(@Body User user);

    @FormUrlEncoded
    @POST("user/login")
    Call<UserMessage> login(@Field("phone") String phone, @Field("password") String password);
}
