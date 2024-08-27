package com.drew.metadata.flir;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class FlirCameraInfoDirectory extends Directory {
    public static final int TAG_EMISSIVITY = 32;
    public static final int TAG_OBJECT_DISTANCE = 36;
    public static final int TAG_REFLECTED_APPARENT_TEMPERATURE = 40;
    public static final int TAG_ATMOSPHERIC_TEMPERATURE = 44;
    public static final int TAG_IR_WINDOW_TEMPERATURE = 48;
    public static final int TAG_IR_WINDOW_TRANSMISSION = 52;
    public static final int TAG_RELATIVE_HUMIDITY = 60;
    public static final int TAG_PLANCK_R1 = 88;
    public static final int TAG_PLANCK_B = 92;
    public static final int TAG_PLANCK_F = 96;
    public static final int TAG_ATMOSPHERIC_TRANS_ALPHA1 = 112;
    public static final int TAG_ATMOSPHERIC_TRANS_ALPHA2 = 116;
    public static final int TAG_ATMOSPHERIC_TRANS_BETA1 = 120;
    public static final int TAG_ATMOSPHERIC_TRANS_BETA2 = 124;
    public static final int TAG_ATMOSPHERIC_TRANS_X = 128;
    public static final int TAG_CAMERA_TEMPERATURE_RANGE_MAX = 144;
    public static final int TAG_CAMERA_TEMPERATURE_RANGE_MIN = 148;
    public static final int TAG_CAMERA_TEMPERATURE_MAX_CLIP = 152;
    public static final int TAG_CAMERA_TEMPERATURE_MIN_CLIP = 156;
    public static final int TAG_CAMERA_TEMPERATURE_MAX_WARN = 160;
    public static final int TAG_CAMERA_TEMPERATURE_MIN_WARN = 164;
    public static final int TAG_CAMERA_TEMPERATURE_MAX_SATURATED = 168;
    public static final int TAG_CAMERA_TEMPERATURE_MIN_SATURATED = 172;
    public static final int TAG_CAMERA_MODEL = 212;
    public static final int TAG_CAMERA_PART_NUMBER = 244;
    public static final int TAG_CAMERA_SERIAL_NUMBER = 260;
    public static final int TAG_CAMERA_SOFTWARE = 276;
    public static final int TAG_LENS_MODEL = 368;
    public static final int TAG_LENS_PART_NUMBER = 400;
    public static final int TAG_LENS_SERIAL_NUMBER = 416;
    public static final int TAG_FIELD_OF_VIEW = 436;
    public static final int TAG_FILTER_MODEL = 492;
    public static final int TAG_FILTER_PART_NUMBER = 508;
    public static final int TAG_FILTER_SERIAL_NUMBER = 540;
    public static final int TAG_PLANCK_O = 776;
    public static final int TAG_PLANCK_R2 = 780;
    public static final int TAG_RAW_VALUE_RANGE_MIN = 784;
    public static final int TAG_RAW_VALUE_RANGE_MAX = 786;
    public static final int TAG_RAW_VALUE_MEDIAN = 824;
    public static final int TAG_RAW_VALUE_RANGE = 828;
    public static final int TAG_DATE_TIME_ORIGINAL = 900;
    public static final int TAG_FOCUS_STEP_COUNT = 912;
    public static final int TAG_FOCUS_DISTANCE = 1116;
    public static final int TAG_FRAME_RATE = 1124;

    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    @Override
    public String getName() {
        return "FLIR Camera Info";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }


    static {
        _tagNameMap.put(TAG_EMISSIVITY, "Emissivity");
        _tagNameMap.put(TAG_OBJECT_DISTANCE, "Object Distance");
        _tagNameMap.put(TAG_REFLECTED_APPARENT_TEMPERATURE, "Reflected Apparent Temperature");
        _tagNameMap.put(TAG_ATMOSPHERIC_TEMPERATURE, "Atmospheric Temperature");
        _tagNameMap.put(TAG_IR_WINDOW_TEMPERATURE, "IR Window Temperature");
        _tagNameMap.put(TAG_IR_WINDOW_TRANSMISSION, "IR Window Transmission");
        _tagNameMap.put(TAG_RELATIVE_HUMIDITY, "Relative Humidity");
        _tagNameMap.put(TAG_PLANCK_R1, "Planck R1");
        _tagNameMap.put(TAG_PLANCK_B, "Planck B");
        _tagNameMap.put(TAG_PLANCK_F, "Planck F");
        _tagNameMap.put(TAG_ATMOSPHERIC_TRANS_ALPHA1, "Atmospheric Trans Alpha1");
        _tagNameMap.put(TAG_ATMOSPHERIC_TRANS_ALPHA2, "Atmospheric Trans Alpha2");
        _tagNameMap.put(TAG_ATMOSPHERIC_TRANS_BETA1, "Atmospheric Trans Beta1");
        _tagNameMap.put(TAG_ATMOSPHERIC_TRANS_BETA2, "Atmospheric Trans Beta2");
        _tagNameMap.put(TAG_ATMOSPHERIC_TRANS_X, "Atmospheric Trans X");
        _tagNameMap.put(TAG_CAMERA_TEMPERATURE_RANGE_MAX, "Camera Temperature Range Max");
        _tagNameMap.put(TAG_CAMERA_TEMPERATURE_RANGE_MIN, "Camera Temperature Range Min");
        _tagNameMap.put(TAG_CAMERA_TEMPERATURE_MAX_CLIP, "Camera Temperature Max Clip");
        _tagNameMap.put(TAG_CAMERA_TEMPERATURE_MIN_CLIP, "Camera Temperature Min Clip");
        _tagNameMap.put(TAG_CAMERA_TEMPERATURE_MAX_WARN, "Camera Temperature Max Warn");
        _tagNameMap.put(TAG_CAMERA_TEMPERATURE_MIN_WARN, "Camera Temperature Min Warn");
        _tagNameMap.put(TAG_CAMERA_TEMPERATURE_MAX_SATURATED, "Camera Temperature Max Saturated");
        _tagNameMap.put(TAG_CAMERA_TEMPERATURE_MIN_SATURATED, "Camera Temperature Min Saturated");
        _tagNameMap.put(TAG_CAMERA_MODEL, "Camera Model");
        _tagNameMap.put(TAG_CAMERA_PART_NUMBER, "Camera Part Number");
        _tagNameMap.put(TAG_CAMERA_SERIAL_NUMBER, "Camera Serial Number");
        _tagNameMap.put(TAG_CAMERA_SOFTWARE, "Camera Software");
        _tagNameMap.put(TAG_LENS_MODEL, "Lens Model");
        _tagNameMap.put(TAG_LENS_PART_NUMBER, "Lens Part Number");
        _tagNameMap.put(TAG_LENS_SERIAL_NUMBER, "Lens Serial Number");
        _tagNameMap.put(TAG_FIELD_OF_VIEW, "Field Of View");
        _tagNameMap.put(TAG_FILTER_MODEL, "Filter Model");
        _tagNameMap.put(TAG_FILTER_PART_NUMBER, "Filter Part Number");
        _tagNameMap.put(TAG_FILTER_SERIAL_NUMBER, "Filter Serial Number");
        _tagNameMap.put(TAG_PLANCK_O, "Planck O");
        _tagNameMap.put(TAG_PLANCK_R2, "Planck R2");
        _tagNameMap.put(TAG_RAW_VALUE_RANGE_MIN, "Raw Value Range Min");
        _tagNameMap.put(TAG_RAW_VALUE_RANGE_MAX, "Raw Value Range Max");
        _tagNameMap.put(TAG_RAW_VALUE_MEDIAN, "Raw Value Median");
        _tagNameMap.put(TAG_RAW_VALUE_RANGE, "Raw Value Range");
        _tagNameMap.put(TAG_DATE_TIME_ORIGINAL, "Date Time Original");
        _tagNameMap.put(TAG_FOCUS_STEP_COUNT, "Focus Step Count");
        _tagNameMap.put(TAG_FOCUS_DISTANCE, "Focus Distance");
        _tagNameMap.put(TAG_FRAME_RATE, "Frame Rate");
    }

    public FlirCameraInfoDirectory() {
        this.setDescriptor(new FlirCameraInfoDescriptor(this));
    }

}
