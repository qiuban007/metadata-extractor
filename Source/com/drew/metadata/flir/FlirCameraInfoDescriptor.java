package com.drew.metadata.flir;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.MetadataException;
import com.drew.metadata.TagDescriptor;

import java.text.DecimalFormat;

import static com.drew.metadata.flir.FlirCameraInfoDirectory.*;

public class FlirCameraInfoDescriptor extends TagDescriptor<FlirCameraInfoDirectory> {

    private final DecimalFormat decimalFormat = new DecimalFormat("#.0");

    public FlirCameraInfoDescriptor(@NotNull FlirCameraInfoDirectory directory) {
        super(directory);
    }


    @Override
    public String getDescription(int tagType) {
        switch (tagType) {
            case TAG_REFLECTED_APPARENT_TEMPERATURE:
            case TAG_ATMOSPHERIC_TEMPERATURE:
            case TAG_IR_WINDOW_TEMPERATURE:
            case TAG_CAMERA_TEMPERATURE_RANGE_MAX:
            case TAG_CAMERA_TEMPERATURE_RANGE_MIN:
            case TAG_CAMERA_TEMPERATURE_MAX_CLIP:
            case TAG_CAMERA_TEMPERATURE_MIN_CLIP:
            case TAG_CAMERA_TEMPERATURE_MAX_WARN:
            case TAG_CAMERA_TEMPERATURE_MIN_WARN:
            case TAG_CAMERA_TEMPERATURE_MAX_SATURATED:
            case TAG_CAMERA_TEMPERATURE_MIN_SATURATED:
                return kelvinToCelsius(tagType);
            case TAG_RELATIVE_HUMIDITY:
                return relativeHumidity(tagType);
            default:
                return super.getDescription(tagType);
        }
    }

    private String kelvinToCelsius(int tagType)  {
        float kelvin = 0;
        try {
            kelvin = _directory.getFloat(tagType);
        } catch (MetadataException e) {
            throw new RuntimeException(e);
        }
        float celsius = kelvin - 273.15f;
        return decimalFormat.format(celsius) + " C";
    }

    private String relativeHumidity(int tagType) {
        float humidity = 0;
        try {
            humidity = _directory.getFloat(tagType);
        } catch (MetadataException e) {
            throw new RuntimeException(e);
        }
        float value = (humidity > 2 ? humidity / 100 : humidity);
        return decimalFormat.format(value * 100) + " %";
    }

}
