package com.example.chillmusic.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.adapter.CategoryAdapter;
import com.example.chillmusic.models.Category;
import com.example.chillmusic.service.ApiService;
import com.example.chillmusic.service.CategoryResponse;
import com.example.chillmusic.service.RetrofitClient;
import com.example.chillmusic.ui.player.CategoryPlayerActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.rv_sounds);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, category -> {
            // Khi click vào category, mở CategoryPlayerActivity
            Intent intent = new Intent(getContext(), CategoryPlayerActivity.class);
            intent.putExtra("CATEGORY_ID", Long.valueOf(category.getId()));  // Chuyển ID category
            intent.putExtra("CATEGORY_TITLE", category.getTitle());
            intent.putExtra("avatar", category.getAvatar());
            Log.d("HomeFragment", "Title: " + category.getTitle() + " | Avatar: " + category.getAvatar());


            startActivity(intent);
        });

        recyclerView.setAdapter(categoryAdapter);

        fetchCategories();

        return view;
    }

    private void fetchCategories() {
        ApiService api = RetrofitClient.getApiService();

        api.getAllCategories(1, 20, "", "desc", "created_at")
                .enqueue(new Callback<CategoryResponse>() {
                    @Override
                    public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Category> categories = response.body().getData().getData();
                            Log.d("✅", "Số category nhận được: " + categories.size());

                            categoryList.clear();
                            categoryList.addAll(categories);
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("❌", "API lỗi: " + response.code());
                            Toast.makeText(getContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryResponse> call, Throwable t) {
                        Log.e("❌", "API thất bại: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi mạng khi tải danh mục", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
