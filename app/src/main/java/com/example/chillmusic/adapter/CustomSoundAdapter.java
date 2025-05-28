package com.example.chillmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chillmusic.R;
import com.example.chillmusic.models.SoundItem;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CustomSoundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    public interface OnSoundClickListener {
        void onSoundClick(SoundItem item);
    }

    private final List<Object> items;
    private final OnSoundClickListener listener;
    private final Context context;

    public CustomSoundAdapter(Context context, List<Object> mixedItems, OnSoundClickListener listener) {
        this.context = context;
        this.items = mixedItems;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_custom_group, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sound_custom, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            String groupName = (String) items.get(position);
            ((HeaderViewHolder) holder).tvGroupTitle.setText(groupName);
        } else {
            SoundItem item = (SoundItem) items.get(position);
            ItemViewHolder viewHolder = (ItemViewHolder) holder;

            viewHolder.tvLabel.setText(item.getName());

            // 🔥 Dùng Glide để load ảnh từ URL thay vì iconResId
            String baseUrl = "http://10.0.2.2:5000";

            String imageToLoad = null;
            if (item.getIconUrl() != null && !item.getIconUrl().isEmpty()) {
                imageToLoad = baseUrl + item.getIconUrl();
            } else if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                imageToLoad = baseUrl + item.getImageUrl();
            }

            if (imageToLoad != null) {
                Glide.with(context)
                        .load(imageToLoad)
                        //.placeholder(R.drawable.acoustic_guitar) // nếu muốn hiển thị tạm
                        .into(viewHolder.imgIcon);
            } else {
                viewHolder.imgIcon.setImageDrawable(null);
            }





            viewHolder.imgLock.setVisibility(item.isLocked() ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSoundClick(item);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tv_group_title);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgIcon;
        TextView tvLabel;
        ImageView imgLock;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            imgLock = itemView.findViewById(R.id.imgLock);
        }
    }
}
