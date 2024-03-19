package com.example.musicplay.api;

import com.example.musicplay.domain.Category;
import com.example.musicplay.domain.CategoryMessage;

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

public interface CategoryApi {

    @POST("category/all")
    Call<List<Category>> getAllCategory();

    @Multipart
    @POST("category/create")
    Call<CategoryMessage> createCategory(@Part("name")RequestBody name, @Part MultipartBody.Part image, @Part("description") RequestBody description);

    @PUT("category/update/{id}")
    Call<CategoryMessage> update(@Path("id") long id, @Body Category category);

    @FormUrlEncoded
    @POST("category/delete")
    Call<CategoryMessage> delete(@Field("id") long id);
}
