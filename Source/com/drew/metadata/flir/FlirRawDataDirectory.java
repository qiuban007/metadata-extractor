package com.drew.metadata.flir;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

public final class FlirRawDataDirectory extends Directory {
    public static final int TAG_RAW_THERMAL_IMAGE_WIDTH = 2;
    public static final int TAG_RAW_THERMAL_IMAGE_HEIGHT = 4;
    public static final int TAG_RAW_THERMAL_IMAGE_TYPE = 34;
    public static final int TAG_RAW_THERMAL_IMAGE = 100;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_RAW_THERMAL_IMAGE_WIDTH, "Raw Thermal Image Width");
        _tagNameMap.put(TAG_RAW_THERMAL_IMAGE_HEIGHT, "Raw Thermal Image Height");
        _tagNameMap.put(TAG_RAW_THERMAL_IMAGE_TYPE, "Raw Thermal Image Type");
        _tagNameMap.put(TAG_RAW_THERMAL_IMAGE, "Raw Thermal Image");
    }

    public FlirRawDataDirectory() {
        super.setDescriptor(new TagDescriptor<>(this));
    }

    @Override
    public String getName() {
        return "FLIR Raw Data";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
