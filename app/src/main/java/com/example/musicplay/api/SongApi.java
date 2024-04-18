package com.example.musicplay.api;

import com.example.musicplay.domain.SongMessage;
import com.example.musicplay.domain.SongUpdate;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface SongApi {

    @Multipart
    @POST("song/create")
    Call<SongMessage> createSong(@Part MultipartBody.Part file, @Part MultipartBody.Part image, @Part("name") String name, @Part("author") String author, @Part("singer") String singer, @Part("category_id") long category_id);

    @FormUrlEncoded
    @POST("song/delete")
    Call<SongMessage> deleteSong(@Field("songId") Long songId);

    @POST("song/all")
    Call<SongMessage> getAllSong();

    @FormUrlEncoded
    @POST("song/getByName")
    Call<SongMessage> getByName(@Field("name") String name);

    @FormUrlEncoded
    @POST("song/getSongOfCategory")
    Call<SongMessage> getSongOfCategory(@Field("category_id") Long category_id);

    @PUT("song/update/{id}")
    Call<SongMessage> update(@Path("id") Long id, @Body SongUpdate song);

    @FormUrlEncoded
    @POST("song/getListId")
    Call<SongMessage> getListId(@Field("ids") List<Long> ids);
}
