package com.example.chillmusic.utils;

import com.example.chillmusic.R;
import com.example.chillmusic.models.Sound;
import com.example.chillmusic.models.SoundDto;

public class SoundMapper {
    public static Sound fromDto(SoundDto dto) {
        int defaultImageRes = getDefaultImageByCategory(dto.getCategory());
        int defaultAudioRes = 0;

        return new Sound(defaultImageRes, dto.getTitle(), defaultAudioRes);
    }

    private static int getDefaultImageByCategory(String category) {
        switch (category.toLowerCase()) {
            case "rain": return R.drawable.rain_in_forest;
            case "cafe": return R.drawable.cafe_ambience;
            case "ocean": return R.drawable.ocean_waves;
            default: return R.drawable.ddc;
        }
    }
}
