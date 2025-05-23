package com.example.chillmusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.models.FreeApp;

import java.util.List;

public class MoreAppsAdapter extends RecyclerView.Adapter<MoreAppsAdapter.ViewHolder>{
    private final List<FreeApp> appList;
    private final Context context;

    public MoreAppsAdapter(List<FreeApp> appList, Context context) {
        this.appList = appList;
        this.context = context;
    }

    @NonNull
    @Override
    public MoreAppsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_free_app, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MoreAppsAdapter.ViewHolder holder, int position) {
        FreeApp app = appList.get(position);
        holder.tvTitle.setText(app.getTitle());
        holder.tvDescription.setText(app.getDescription());
        holder.ivIcon.setImageResource(app.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(app.getUrl()));
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return appList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivAppIcon);
            tvTitle = itemView.findViewById(R.id.tvAppTitle);
            tvDescription = itemView.findViewById(R.id.tvAppDescription);
        }
    }
}