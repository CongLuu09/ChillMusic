package com.example.chillmusic.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chillmusic.R;
import com.example.chillmusic.data.db.AppDatabase;
import com.example.chillmusic.models.LayerSound;

import java.io.IOException;
import java.util.List;

public class PlayLayerAdapter extends RecyclerView.Adapter<PlayLayerAdapter.ViewHolder> {
    private final List<LayerSound> layers;
    private final Context context;

    public PlayLayerAdapter(Context context, List<LayerSound> layers) {
        this.context = context;
        this.layers = layers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layer_sound, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LayerSound layer = layers.get(position);

        holder.tvLayerName.setText(layer.getName());

        String imageUrl = layer.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.airplane_flying)
                    .error(R.drawable.bird_chirping)
                    .into(holder.imgLayerIcon);
        } else {
            if (layer.getIconResId() != 0) {
                holder.imgLayerIcon.setImageResource(layer.getIconResId());
            } else {
                holder.imgLayerIcon.setImageDrawable(null);
            }
        }

        holder.seekBarVolume.setProgress((int) (layer.getVolume() * 100));

        MediaPlayer player = layer.getMediaPlayer();
        if (player == null) {
            if (layer.getFileUrl() != null && !layer.getFileUrl().isEmpty()) {
                player = new MediaPlayer();
                try {
                    player.setDataSource(layer.getFileUrl());
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setLooping(true);
                    player.setOnPreparedListener(MediaPlayer::start);
                    player.prepareAsync();
                } catch (IOException e) {
                    Log.e("PlayLayerAdapter", "Failed to create MediaPlayer from URL", e);
                    player = null;
                }
            } else if (layer.getSoundResId() != 0) {
                player = MediaPlayer.create(context, layer.getSoundResId());
                if (player != null) {
                    player.setLooping(true);
                    player.setVolume(layer.getVolume(), layer.getVolume());
                    player.start();
                }
            }
            layer.setMediaPlayer(player);
        }

        holder.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            MediaPlayer player = layer.getMediaPlayer();
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                layer.setVolume(volume);
                if (player != null) {
                    player.setVolume(volume, volume);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        holder.btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Đổi tên âm thanh");

            final EditText input = new EditText(context);
            input.setText(layer.getName());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Lưu", (dialog, which) -> {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty()) {
                    AppDatabase db = new AppDatabase(context);
                    db.updateSoundName(layer.getName(), newName, "");
                    layer.setName(newName);
                    notifyItemChanged(holder.getAdapterPosition());
                }
            });

            builder.setNegativeButton("Huỷ", null);
            builder.show();
        });


        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();

            new AlertDialog.Builder(context)
                    .setTitle("Xoá âm thanh")
                    .setMessage("Bạn có chắc muốn xoá \"" + layer.getName() + "\" không?")
                    .setPositiveButton("Xoá", (dialog, which) -> {
                        AppDatabase db = new AppDatabase(context);
                        db.deleteSoundByName(layer.getName(), "");

                        releaseLayer(layer);
                        layers.remove(pos);
                        notifyItemRemoved(pos);
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });

    }



    @Override
    public int getItemCount() {
        return layers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLayerIcon, btnEdit, btnDelete;
        TextView tvLayerName;
        SeekBar seekBarVolume;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLayerIcon = itemView.findViewById(R.id.imgLayerIcon);
            tvLayerName = itemView.findViewById(R.id.tvLayerName);
            seekBarVolume = itemView.findViewById(R.id.seekBarVolume);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void addLayer(LayerSound layer, MediaPlayer player) {
        String newName = layer.getName().trim().toLowerCase();
        for (LayerSound l : layers) {
            if (l.getName().trim().toLowerCase().equals(newName)) {
                Toast.makeText(context, "Đã tồn tại âm thanh \"" + layer.getName() + "\"", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        layers.add(layer);
        notifyItemInserted(layers.size() - 1);

        if (player != null) {
            player.setLooping(true);
            player.setVolume(layer.getVolume(), layer.getVolume());
            player.start();
            layer.setMediaPlayer(player);
        }

        AppDatabase db = new AppDatabase(context);
        if (!db.isSoundExists(layer.getName(), "")) {
            db.insertSound(layer.getName(), layer.getIconResId(), layer.getSoundResId(), "");
        }
    }





    public void removeLayer(String soundName) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getName().equals(soundName)) {
                releaseLayer(layers.get(i));
                layers.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void removeLayer(LayerSound sound) {
        removeLayer(sound.getName());
    }

    public void releaseAllPlayers() {
        for (LayerSound layer : layers) {
            releaseLayer(layer);
        }
    }

    private void releaseLayer(LayerSound layer) {
        MediaPlayer player = layer.getMediaPlayer();
        if (player != null) {
            player.stop();
            player.release();
            layer.setMediaPlayer(null);
        }
    }
}