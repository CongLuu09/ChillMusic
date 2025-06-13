package com.example.chillmusic.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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


            final String baseUrl = "https://a8f0-42-113-99-170.ngrok-free.app/";
            String imageUrl = item.getImageUrl();
            String imageToLoad = null;
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    imageToLoad = imageUrl;
                } else {
                    if (!imageUrl.startsWith("/")) {
                        imageUrl = "/" + imageUrl;
                    }
                    imageToLoad = baseUrl + imageUrl;
                }
            }

            Log.d("CustomSoundAdapter", "Loading image URL: " + imageToLoad);

            if (imageToLoad != null) {
                String finalImageToLoad = imageToLoad;
                Glide.with(context)
                        .load(imageToLoad)
                        .placeholder(R.drawable.airplane_flying)
                        .error(R.drawable.bird_chirping)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                Log.e("CustomSoundAdapter", "❌ Image load failed: " + finalImageToLoad, e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("CustomSoundAdapter", "✅ Image loaded: " + finalImageToLoad);
                                return false;
                            }
                        })
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

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupTitle;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tv_group_title);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgIcon;
        TextView tvLabel;
        ImageView imgLock;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            imgLock = itemView.findViewById(R.id.imgLock);
        }
    }
}
