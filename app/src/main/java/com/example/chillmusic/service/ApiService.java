package com.example.chillmusic.service;

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

    // 🔊 Lấy danh sách âm thanh
    @GET("/api/sounds")
    Call<List<SoundDto>> getAllSounds();

    @GET("/api/sounds")
    Call<List<SoundDto>> getSoundsByCategory(@Query("category") String category);

    @GET("/api/sounds")
    Call<List<SoundDto>> getSoundsByIds(@Query("ids") String commaSeparatedIds); // "1,3,5"

    // 💾 Lưu bản phối (Mix)
    @POST("/api/mix")
    Call<MixDto> saveMix(@Body MixCreateRequest request);

    @POST("/api/mix")
    Call<Void> createMix(@Body MixCreateRequest request);


    // 🔁 Lấy danh sách mix theo deviceId
    @GET("/api/mix")
    Call<List<MixDto>> getMixesByDevice(@Query("deviceId") String deviceId);

    // 📝 Cập nhật mix
    @PUT("/api/mix/{id}")
    Call<MixDto> updateMix(@Path("id") int id, @Body MixUpdateRequest request);

    // ❌ Xoá mix
    @DELETE("/api/mix/{id}")
    Call<Void> deleteMix(@Path("id") int id);

    // ⬆️ Upload file ảnh + âm thanh
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
