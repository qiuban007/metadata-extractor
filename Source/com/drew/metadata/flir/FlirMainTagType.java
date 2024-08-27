package com.drew.metadata.flir;

public enum FlirMainTagType {

    UNUSED((short) 0),
    PIXELS((short) 1),
    GAIN_MAP((short) 2),
    OFFS_MAP((short) 3),
    DEAD_MAP((short) 4),
    GAIN_DEAD_MAP((short) 5),
    COARSE_MAP((short) 6),
    IMAGE_MAP((short) 7),

    BASIC_DATA((short) 0x20),
    MEASURE((short) 0x21),
    COLOR_PAL((short) 0x22);

    private short value;

    FlirMainTagType(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }
    public static FlirMainTagType fromValue(int value) {
        for (FlirMainTagType type : FlirMainTagType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
