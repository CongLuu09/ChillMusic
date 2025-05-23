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

import com.example.chillmusic.R;
import com.example.chillmusic.models.SoundItem;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CustomSoundAdapter extends RecyclerView.Adapter<CustomSoundAdapter.ViewHolder> {
    public interface OnSoundClickListener {
        void onSoundClick(SoundItem item);
    }

    private final List<SoundItem> soundList;
    private final OnSoundClickListener listener;
    private final Context context;

    public CustomSoundAdapter(Context context, List<SoundItem> soundList, OnSoundClickListener listener) {
        this.context = context;
        this.soundList = soundList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sound_custom, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomSoundAdapter.ViewHolder holder, int position) {
        SoundItem item = soundList.get(position);

        holder.tvLabel.setText(item.getName());
        holder.imgIcon.setImageResource(item.getIconResId());
        holder.imgLock.setVisibility(item.isLocked() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSoundClick(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return soundList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView imgIcon;
        TextView tvLabel;
        ImageView imgLock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            imgLock = itemView.findViewById(R.id.imgLock);
        }
    }
}
