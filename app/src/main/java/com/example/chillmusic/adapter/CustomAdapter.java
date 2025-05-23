package com.example.chillmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chillmusic.R;
import com.example.chillmusic.models.CustomSound;
import com.example.chillmusic.models.CustomSoundGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_CHILD = 1;

    private final List<Item> items = new ArrayList<>();
    private final Context context;
    private final OnItemClickListener listener;
    private final Set<Integer> activeSoundResIds = new HashSet<>();
    private final Set<Integer> expandedPositions = new HashSet<>();

    public interface OnItemClickListener {
        void onItemClick(CustomSound customSound);
        void onVolumeChange(CustomSound customSound, float volume);
    }

    private static class Item {
        final int type;
        final String groupTitle;
        final CustomSound child;

        Item(int type, String groupTitle, CustomSound child) {
            this.type = type;
            this.groupTitle = groupTitle;
            this.child = child;
        }
    }

    public CustomAdapter(List<CustomSoundGroup> groups, Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        flattenList(groups);
    }

    private void flattenList(List<CustomSoundGroup> groups) {
        items.clear();
        for (CustomSoundGroup group : groups) {
            items.add(new Item(TYPE_GROUP, group.getGroupName(), null));
            for (CustomSound sound : group.getSounds()) {
                items.add(new Item(TYPE_CHILD, null, sound));
            }
        }
    }

    public void setActiveSounds(Set<Integer> activeSoundResIds) {
        this.activeSoundResIds.clear();
        this.activeSoundResIds.addAll(activeSoundResIds);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_GROUP) {
            View view = inflater.inflate(R.layout.item_custom_group, parent, false);
            return new GroupViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_custom_sound, parent, false);
            return new ChildViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);
        if (item.type == TYPE_GROUP) {
            GroupViewHolder groupHolder = (GroupViewHolder) holder;
            groupHolder.tvGroupTitle.setText(item.groupTitle);
        } else {
            ChildViewHolder childHolder = (ChildViewHolder) holder;
            CustomSound sound = item.child;

            childHolder.tvTitle.setText(sound.getTitle());
            childHolder.ivIcon.setImageResource(sound.getImageResId());

            childHolder.seekBarVolume.setVisibility(
                    expandedPositions.contains(position) ? View.VISIBLE : View.GONE
            );

            if (activeSoundResIds.contains(sound.getSoundResId())) {
                childHolder.ivIcon.setBackgroundResource(R.drawable.bg_sound_active);
            } else {
                childHolder.ivIcon.setBackgroundResource(R.drawable.bg_sound_inactive);
            }

            View.OnClickListener clickListener = v -> {
                if (listener != null) {
                    listener.onItemClick(sound);
                }
                if (expandedPositions.contains(position)) {
                    expandedPositions.remove(position);
                } else {
                    expandedPositions.add(position);
                }
                notifyItemChanged(position);
            };

            childHolder.ivIcon.setOnClickListener(clickListener);
            childHolder.itemView.setOnClickListener(clickListener);

            childHolder.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && listener != null) {
                        float volume = progress / 100f;
                        listener.onVolumeChange(sound, volume);
                    }
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupTitle;
        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupTitle = itemView.findViewById(R.id.tv_group_title);
        }
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;
        SeekBar seekBarVolume;
        ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_sound_title);
            ivIcon = itemView.findViewById(R.id.img_sound_icon);
            seekBarVolume = itemView.findViewById(R.id.seekBarVolume);
        }
    }
}