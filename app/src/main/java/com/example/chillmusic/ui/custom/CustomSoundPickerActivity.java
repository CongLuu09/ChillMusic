package com.example.chillmusic.ui.custom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.adapter.CustomSoundAdapter;
import com.example.chillmusic.models.CustomSound;
import com.example.chillmusic.models.CustomSoundGroup;
import com.example.chillmusic.models.SoundDto;
import com.example.chillmusic.models.SoundItem;
import com.example.chillmusic.service.ApiService;
import com.example.chillmusic.service.RetrofitClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomSoundPickerActivity extends AppCompatActivity implements CustomSoundAdapter.OnSoundClickListener {

    private RecyclerView recyclerViewCustomSounds;
    private CustomSoundAdapter adapter;
    private ImageView btnClose;
    private TextView tvTitle;
    private final List<Object> allItems = new ArrayList<>();
    private final List<SoundDto> onlineSounds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_sound_picker);

        recyclerViewCustomSounds = findViewById(R.id.recyclerViewCustomSounds);
        btnClose = findViewById(R.id.btnClose);
        tvTitle = findViewById(R.id.tvTitle);

        setupRecyclerView();
        setupListeners();
        fetchOnlineSounds();
    }

    private void setupListeners() {
        btnClose.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        adapter = new CustomSoundAdapter(this, allItems, this);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);
                return viewType == CustomSoundAdapter.VIEW_TYPE_HEADER ? 3 : 1;
            }
        });
        recyclerViewCustomSounds.setLayoutManager(layoutManager);
        recyclerViewCustomSounds.setAdapter(adapter);
    }

    private void fetchOnlineSounds() {
        ApiService api = RetrofitClient.getApiService();
        api.getAllSounds().enqueue(new Callback<List<SoundDto>>() {
            @Override
            public void onResponse(Call<List<SoundDto>> call, Response<List<SoundDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onlineSounds.clear();
                    onlineSounds.addAll(response.body());
                    updateCombinedSoundItems();
                }
            }

            @Override
            public void onFailure(Call<List<SoundDto>> call, Throwable t) {
                Log.e("API", "❌ Failed to fetch sounds from server", t);
            }
        });
    }

    private void updateCombinedSoundItems() {
        allItems.clear();
        String baseUrl = "http://10.0.2.2:3000/";

        if (!onlineSounds.isEmpty()) {
            // Map category -> List<SoundItem>
            Map<String, List<SoundItem>> groupedByCategory = new LinkedHashMap<>();

            for (SoundDto dto : onlineSounds) {
                // Chuẩn hóa URL file âm thanh
                String fullFileUrl = null;
                if (dto.getFileUrl() != null) {
                    String fileUrl = dto.getFileUrl();
                    fullFileUrl = fileUrl.startsWith("/") ? baseUrl + fileUrl.substring(1) : baseUrl + fileUrl;
                }

                // Chuẩn hóa URL ảnh
                String fullImageUrl = null;
                if (dto.getImageUrl() != null) {
                    String imageUrl = dto.getImageUrl();
                    fullImageUrl = imageUrl.startsWith("/") ? baseUrl + imageUrl.substring(1) : baseUrl + imageUrl;
                }

                // Tạo SoundItem
                SoundItem soundItem = new SoundItem(
                        dto.getId(),
                        0, // iconResId = 0 vì ảnh load từ URL
                        true,
                        dto.getName(),
                        0,
                        fullFileUrl,
                        fullImageUrl
                );

                // Lấy category, nếu null hoặc trống thì đặt category mặc định
                String category = dto.getCategory();
                if (category == null || category.isEmpty()) {
                    category = "Others";
                }

                // Thêm vào map
                if (!groupedByCategory.containsKey(category)) {
                    groupedByCategory.put(category, new ArrayList<>());
                }
                groupedByCategory.get(category).add(soundItem);
            }

            // Chuyển map thành danh sách hỗn hợp có header (String) và item (SoundItem)
            for (String category : groupedByCategory.keySet()) {
                allItems.add(category);  // header là tên nhóm
                allItems.addAll(groupedByCategory.get(category));
            }
        }

        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }



    @Override
    public void onSoundClick(SoundItem item) {
        if (item == null) return;
        returnResult(item);
    }

    private void returnResult(SoundItem item) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("name", item.getName());
        resultIntent.putExtra("iconResId", item.getIconResId());
        resultIntent.putExtra("soundResId", item.getSoundResId());
        resultIntent.putExtra("soundId", item.getId());
        resultIntent.putExtra("fileUrl", item.getFileUrl());
        resultIntent.putExtra("imageUrl", item.getImageUrl()); // Trả về URL ảnh icon
        setResult(RESULT_OK, resultIntent);
        finish();
    }


}
