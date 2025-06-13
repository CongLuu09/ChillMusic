package com.example.chillmusic.service;

import com.example.chillmusic.models.Category;
import com.example.chillmusic.models.MixCreateRequest;
import com.example.chillmusic.models.MixDto;
import com.example.chillmusic.models.MixUpdateRequest;
import com.example.chillmusic.models.SoundDto;
import com.example.chillmusic.models.UploadResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("/api/sounds")
    Call<List<SoundDto>> getAllSounds();

    @GET("/api/sounds")
    Call<List<SoundDto>> getSoundsByCategory(@Query("category") String category);

    @GET("/api/sounds")
    Call<List<SoundDto>> getSoundsByIds(@Query("ids") String commaSeparatedIds);


    @POST("/api/mix")
    Call<MixDto> createMix(@Body MixCreateRequest request);

    @GET("/api/mix")
    Call<List<MixDto>> getMixesByDevice(@Query("deviceId") String deviceId);

    @PUT("/api/mix/{id}")
    Call<MixDto> updateMix(@Path("id") int id, @Body MixUpdateRequest request);

    @DELETE("/api/mix/{id}")
    Call<Void> deleteMix(@Path("id") int id);

    @GET("/api/categories")
    Call<List<Category>> getAllCategories();



    @Multipart
    @POST("/api/upload")
    Call<UploadResponse> uploadSound(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part fileImage,
            @Part MultipartBody.Part fileSound
    );

    @Multipart
    @POST("/api/upload/audio")
    Call<UploadResponse> uploadAudio(@Part MultipartBody.Part file);

}
