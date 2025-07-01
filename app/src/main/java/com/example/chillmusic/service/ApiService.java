package com.example.chillmusic.service;

import com.example.chillmusic.models.*;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ⚡ 1. Lấy tất cả sound
    @GET("/api/v1/music")
    Call<SoundResponse> getAllSounds();

    // ⚡ 2. Lấy sound theo categoryId mới
    @GET("api/v1/sounds/by-category")
    Call<ApiResponse<List<SoundDto>>> getSoundsByCategoryId(@Query("categoryId") int categoryId);



    // ⚡ 3. Lấy sound theo danh sách ID
    @GET("/api/sounds/by-ids")
    Call<List<SoundDto>> getSoundsByIds(@Query("ids") String commaSeparatedIds);

    @GET("/api/sounds/by-name")
    Call<SoundDto> getSoundByName(@Query("name") String name);


    // ⚡ 4. Tạo mix
    @POST("/api/mix")
    Call<MixDto> createMix(@Body MixCreateRequest request);

    // ⚡ 5. Lấy mix theo thiết bị
    @GET("/api/mix")
    Call<List<MixDto>> getMixesByDevice(@Query("deviceId") String deviceId);

    // ⚡ 6. Cập nhật mix
    @PUT("/api/mix/{id}")
    Call<MixDto> updateMix(@Path("id") int id, @Body MixUpdateRequest request);

    // ⚡ 7. Xoá mix
    @DELETE("/api/mix/{id}")
    Call<Void> deleteMix(@Path("id") int id);

    // ⚡ 8. Lấy danh sách category (có thể có phân trang hoặc không)
    @GET("/api/v1/categories")
    Call<CategoryResponse> getAllCategories(
            @Query("page") Integer page,
            @Query("per_page") Integer perPage,
            @Query("search") String search,
            @Query("order") String order,
            @Query("order_by") String orderBy
    );

    // ⚡ 9. Đăng nhập
    @FormUrlEncoded
    @POST("/api/login")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );

    // ⚡ 10. Upload ảnh + âm thanh
    @Multipart
    @POST("/api/upload")
    Call<UploadResponse> uploadSound(
            @Part("name") RequestBody name,
            @Part MultipartBody.Part fileImage,
            @Part MultipartBody.Part fileSound
    );

    // ⚡ 11. Upload riêng file audio
    @Multipart
    @POST("/api/upload/audio")
    Call<UploadResponse> uploadAudio(@Part MultipartBody.Part file);
}
