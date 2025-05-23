package com.example.chillmusic.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.data.db.AppDatabase;
import com.example.chillmusic.models.LayerSound;

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
        holder.imgLayerIcon.setImageResource(layer.getIconResId());
        holder.seekBarVolume.setProgress((int) (layer.getVolume() * 100));


        if (layer.getMediaPlayer() == null) {
            MediaPlayer player = MediaPlayer.create(context, layer.getSoundResId());
            player.setLooping(true);
            player.setVolume(layer.getVolume(), layer.getVolume());
            player.start();
            layer.setMediaPlayer(player);
        }

        MediaPlayer player = layer.getMediaPlayer();


        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                layer.setVolume(volume);
                if (player != null) {
                    player.setVolume(volume, volume);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    @Override
    public int getItemCount() {
        return layers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvName;
        SeekBar seekBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgLayerIcon);
            tvName = itemView.findViewById(R.id.tvLayerName);
            seekBar = itemView.findViewById(R.id.seekBarVolume);
        }
    }

    public void addLayer(LayerSound layer) {
        layers.add(layer);
        notifyItemInserted(layers.size() - 1);


        MediaPlayer player = MediaPlayer.create(context, layer.getSoundResId());
        player.setLooping(true);
        player.setVolume(layer.getVolume(), layer.getVolume());
        player.start();
        layer.setMediaPlayer(player);
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
