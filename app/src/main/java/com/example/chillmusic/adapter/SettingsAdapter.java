package com.example.chillmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.models.SettingItem;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder>{
    private final List<SettingItem> items;
    private final Context context;

    private final OnSettingClickListener listener;

    public SettingsAdapter( List<SettingItem> items, Context context, OnSettingClickListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    public interface OnSettingClickListener {
        void onSettingClick(SettingItem item);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_setting, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SettingItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());

        if (item.hasSubtitle()) {
            holder.tvSubtitle.setText(item.getSubtitle());
            holder.tvSubtitle.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubtitle.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSettingClick(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvSettingTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSettingSubtitle);
        }
    }
}
