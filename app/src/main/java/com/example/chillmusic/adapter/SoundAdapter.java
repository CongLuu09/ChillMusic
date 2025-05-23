package com.example.chillmusic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.models.Sound;

import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> {
    private List<Sound> soundList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Sound sound);
    }

    public SoundAdapter( List<Sound> soundList, OnItemClickListener listener) {
        this.soundList = soundList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sound, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sound sound = soundList.get(position);
        holder.tvSoundTitle.setText(sound.getTitle());
        holder.imgSound.setImageResource(sound.getImageResId());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sound);
            }
        });
    }


    @Override
    public int getItemCount() {
        return soundList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgSound;
        TextView tvSoundTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSound = itemView.findViewById(R.id.imgSound);
            tvSoundTitle = itemView.findViewById(R.id.tvSoundTitle);
        }
    }
}
