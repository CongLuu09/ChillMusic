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

    private final Context context;
    private final List<Object> items;
    private final OnSoundClickListener listener;

    public CustomSoundAdapter(Context context, List<Object> items, OnSoundClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_custom_group, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_sound_custom, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            String groupTitle = (String) items.get(position);
            ((HeaderViewHolder) holder).tvGroupTitle.setText(groupTitle);

        } else if (holder instanceof ItemViewHolder) {
            SoundItem item = (SoundItem) items.get(position);
            ItemViewHolder vh = (ItemViewHolder) holder;

            vh.tvLabel.setText(item.getName());

            String imageUrlRaw = item.getImageUrl();
            String imageUrl = null;

            if (imageUrlRaw != null && !imageUrlRaw.isEmpty()) {
                if (imageUrlRaw.startsWith("http://") || imageUrlRaw.startsWith("https://")) {
                    imageUrl = imageUrlRaw;
                } else {
                    imageUrl = "https://sleepchills.kenhtao.site/" + imageUrlRaw.replaceFirst("^/", "");
                }

                final String finalImageUrl = imageUrl; // 👈 FIX: make final for inner class

                Glide.with(context)
                        .load(finalImageUrl)
                        .placeholder(R.drawable.airplane_flying)
                        .error(R.drawable.bird_chirping)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                Log.e("CustomSoundAdapter", "❌ Load failed: " + finalImageUrl, e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(vh.imgIcon);
            }
            else {
                vh.imgIcon.setImageResource(R.drawable.bird_chirping); // fallback if null
            }

            vh.imgLock.setVisibility(item.isLocked() ? View.VISIBLE : View.GONE);

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
