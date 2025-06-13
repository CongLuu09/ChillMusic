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
import com.example.chillmusic.service.RetrofitClient;
import com.example.chillmusic.ui.player.CategoryPlayerActivity;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.rv_sounds);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        fetchCategories();

        return view;
    }

    private void fetchCategories() {
        ApiService api = RetrofitClient.getApiService();

        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    Log.d("API", "Fetched categories: " + new Gson().toJson(categories));

                    categoryAdapter = new CategoryAdapter(getContext(), categories, category -> {
                        Intent intent = new Intent(getContext(), CategoryPlayerActivity.class);
                        intent.putExtra("categoryName", category.getName());
                        intent.putExtra("soundUrl", category.getSoundUrl());
                        intent.putExtra("imageUrl", category.getImageUrl());
                        startActivity(intent);
                    });

                    recyclerView.setAdapter(categoryAdapter);
                } else {
                    Log.e("API", "Không thể tải danh sách category: " + response.code());
                    Toast.makeText(getContext(), "Không thể tải danh sách category", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {
                Log.e("HomeFragment", "Lỗi mạng khi tải danh sách category: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }
}
