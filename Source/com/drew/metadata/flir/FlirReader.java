package com.drew.metadata.flir;

import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.jpeg.JpegSegmentType;
import com.drew.lang.ByteArrayReader;
import com.drew.lang.RandomAccessReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static com.drew.metadata.flir.FlirCameraInfoDirectory.*;
import static com.drew.metadata.flir.FlirRawDataDirectory.*;

public class FlirReader implements JpegSegmentMetadataReader, MetadataReader {
    public boolean ExtractRawThermalImage = true;
    public static final String JPEG_SEGMENT_PREAMBLE = "FLIR\0";
    private static final byte[] PREAMBLE_BYTES = JPEG_SEGMENT_PREAMBLE.getBytes();

    @NotNull
    public Iterable<JpegSegmentType> getSegmentTypes() {
        return Collections.singletonList(JpegSegmentType.APP1);
    }

    @Override
    public void readJpegSegments(@NotNull Iterable<byte[]> segments, @NotNull Metadata metadata, @NotNull JpegSegmentType segmentType) {
        byte[] preamble = PREAMBLE_BYTES;
        int preambleLength = preamble.length + 3;
        int length = 0;
        for (byte[] segmentBytes : segments) {
            if (startsWith(segmentBytes, preamble)) {
                length += segmentBytes.length - preambleLength;
            }
        }
        if (length == 0) {
            return;
        }
        try (ByteArrayOutputStream merged = new ByteArrayOutputStream()) {
            for (byte[] segmentBytes : segments) {
                if (startsWith(segmentBytes, preamble)) {
                    merged.write(segmentBytes, preambleLength, segmentBytes.length - preambleLength);
                }
            }
            extract(new ByteArrayReader(merged.toByteArray()), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verify the beginning of the image file
     *
     * @param segmentBytes
     * @param preamble
     * @return
     */
    private static boolean startsWith(byte[] segmentBytes, byte[] preamble) {
        return segmentBytes.length >= preamble.length && Arrays.equals(Arrays.copyOfRange(segmentBytes, 0, preamble.length), preamble);
    }

    @Override
    public void extract(RandomAccessReader reader, Metadata metadata) {
        try {
            if (!flirTemperatureDataStartsWith(reader.getBytes(0, 4), new byte[]{(byte) 'F', (byte) 'F', (byte) 'F', 0})) {
                FlirHeaderDirectory directory = new FlirHeaderDirectory();
                directory.addError("Unexpected FFF header bytes.");
                metadata.addDirectory(directory);
                return;
            }
            FlirHeaderDirectory directory = new FlirHeaderDirectory();
            directory.setStringValue(FlirHeaderDirectory.TAG_CREATOR_SOFTWARE, reader.getNullTerminatedStringValue(4, 16, null));
            metadata.addDirectory(directory);

            long baseIndexOffset = reader.getUInt32(24);
            long indexCount = reader.getUInt32(28);
            int indexOffset = checkedToInt(baseIndexOffset);

            for (int i = 0; i < indexCount; i++) {
                FlirMainTagType mainType = FlirMainTagType.fromValue(reader.getUInt16(indexOffset));
                int subType = reader.getUInt16(indexOffset + 2);
                long version = reader.getUInt32(indexOffset + 4);
                long id = reader.getUInt32(indexOffset + 8);
                int dataOffset = reader.getInt32(indexOffset + 12);
                int dataLength = reader.getInt32(indexOffset + 16);
                if (mainType == FlirMainTagType.PIXELS) {
                    RandomAccessReader reader2 = reader.WithShiftedBaseOffset(dataOffset);
                    short marker = reader2.getInt16(0);
                    if (marker > 0x0100)
                        reader2 = reader2.WithByteOrder(!reader2.isMotorolaByteOrder());
                    FlirRawDataDirectory rawDataDirectory = new FlirRawDataDirectory();

                    int width = reader2.getInt16(TAG_RAW_THERMAL_IMAGE_WIDTH);
                    int height = reader2.getInt16(TAG_RAW_THERMAL_IMAGE_HEIGHT);

                    rawDataDirectory.setInt(TAG_RAW_THERMAL_IMAGE_WIDTH, width);
                    rawDataDirectory.setInt(TAG_RAW_THERMAL_IMAGE_HEIGHT, height);
                    if (ExtractRawThermalImage) {
                        rawDataDirectory.setByteArray(TAG_RAW_THERMAL_IMAGE, reader2.getBytes(32, dataLength - 32));
                        byte[] thermalImageData = reader2.getBytes(32, dataLength - 32);

                        if (flirTemperatureDataStartsWith(thermalImageData, new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', (byte) 0x1A, '\n'})) {
                            rawDataDirectory.setString(TAG_RAW_THERMAL_IMAGE_TYPE, "PNG");
                        } else if (startsWith(thermalImageData, new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF})) {
                            rawDataDirectory.setString(TAG_RAW_THERMAL_IMAGE_TYPE, "JPG");
                        } else if (thermalImageData.length != width * height * 2) {
                            rawDataDirectory.setString(TAG_RAW_THERMAL_IMAGE_TYPE, "DAT");
                        } else {
                            rawDataDirectory.setInt(TAG_RAW_THERMAL_IMAGE_TYPE, reader2.getInt16(TAG_RAW_THERMAL_IMAGE_TYPE));
                        }
                    }
                    metadata.addDirectory(rawDataDirectory);
                } else if (mainType == FlirMainTagType.BASIC_DATA) {
                    RandomAccessReader reader3 = reader.WithShiftedBaseOffset(dataOffset);
                    short int16 = reader3.getInt16(0);
                    if (int16 > 0x0100)
                        reader3 = reader3.WithByteOrder(!reader3.isMotorolaByteOrder());
                    FlirCameraInfoDirectory infoDirectory = new FlirCameraInfoDirectory();

                    infoDirectory.setFloat(TAG_EMISSIVITY, reader3.getFloat32(TAG_EMISSIVITY));
                    infoDirectory.setFloat(TAG_OBJECT_DISTANCE, reader3.getFloat32(TAG_OBJECT_DISTANCE));
                    infoDirectory.setFloat(TAG_REFLECTED_APPARENT_TEMPERATURE, reader3.getFloat32(TAG_REFLECTED_APPARENT_TEMPERATURE));

                    infoDirectory.setFloat(TAG_ATMOSPHERIC_TEMPERATURE, reader3.getFloat32(TAG_ATMOSPHERIC_TEMPERATURE));

                    infoDirectory.setFloat(TAG_IR_WINDOW_TEMPERATURE, reader3.getFloat32(TAG_IR_WINDOW_TEMPERATURE));
                    infoDirectory.setFloat(TAG_IR_WINDOW_TRANSMISSION, reader3.getFloat32(TAG_IR_WINDOW_TRANSMISSION));

                    infoDirectory.setFloat(TAG_RELATIVE_HUMIDITY, reader3.getFloat32(TAG_RELATIVE_HUMIDITY));

                    infoDirectory.setString(TAG_PLANCK_R1, new BigDecimal(reader3.getFloat32(TAG_PLANCK_R1)).toPlainString());
                    infoDirectory.setString(TAG_PLANCK_B, new BigDecimal(reader3.getFloat32(TAG_PLANCK_B)).toPlainString());
                    infoDirectory.setString(TAG_PLANCK_F, new BigDecimal(reader3.getFloat32(TAG_PLANCK_F)).toPlainString());

                    infoDirectory.setString(TAG_ATMOSPHERIC_TRANS_ALPHA1, new BigDecimal(reader3.getFloat32(TAG_ATMOSPHERIC_TRANS_ALPHA1)).toPlainString());
                    infoDirectory.setString(TAG_ATMOSPHERIC_TRANS_ALPHA2, new BigDecimal(reader3.getFloat32(TAG_ATMOSPHERIC_TRANS_ALPHA2)).toPlainString());
                    infoDirectory.setString(TAG_ATMOSPHERIC_TRANS_BETA1, new BigDecimal(reader3.getFloat32(TAG_ATMOSPHERIC_TRANS_BETA1)).toPlainString());
                    infoDirectory.setString(TAG_ATMOSPHERIC_TRANS_BETA2, new BigDecimal(reader3.getFloat32(TAG_ATMOSPHERIC_TRANS_BETA2)).toPlainString());
                    infoDirectory.setString(TAG_ATMOSPHERIC_TRANS_X, new BigDecimal(reader3.getFloat32(TAG_ATMOSPHERIC_TRANS_X)).toPlainString());

                    infoDirectory.setFloat(TAG_CAMERA_TEMPERATURE_RANGE_MAX, reader3.getFloat32(TAG_CAMERA_TEMPERATURE_RANGE_MAX));
                    infoDirectory.setFloat(TAG_CAMERA_TEMPERATURE_RANGE_MIN, reader3.getFloat32(TAG_CAMERA_TEMPERATURE_RANGE_MIN));
                    infoDirectory.setFloat(TAG_CAMERA_TEMPERATURE_MAX_CLIP, reader3.getFloat32(TAG_CAMERA_TEMPERATURE_MAX_CLIP));
                    infoDirectory.setFloat(TAG_CAMERA_TEMPERATURE_MIN_CLIP, reader3.getFloat32(TAG_CAMERA_TEMPERATURE_MIN_CLIP));
                    infoDirectory.setFloat(TAG_CAMERA_TEMPERATURE_MAX_WARN, reader3.getFloat32(TAG_CAMERA_TEMPERATURE_MAX_WARN));
                    infoDirectory.setFloat(TAG_CAMERA_TEMPERATURE_MIN_WARN, reader3.getFloat32(TAG_CAMERA_TEMPERATURE_MIN_WARN));
                    infoDirectory.setFloat(TAG_CAMERA_TEMPERATURE_MAX_SATURATED, reader3.getFloat32(TAG_CAMERA_TEMPERATURE_MAX_SATURATED));
                    infoDirectory.setFloat(TAG_CAMERA_TEMPERATURE_MIN_SATURATED, reader3.getFloat32(TAG_CAMERA_TEMPERATURE_MIN_SATURATED));

                    infoDirectory.setStringValue(TAG_CAMERA_MODEL, reader3.getNullTerminatedStringValue(TAG_CAMERA_MODEL, 32, null));
                    infoDirectory.setStringValue(TAG_CAMERA_PART_NUMBER, reader3.getNullTerminatedStringValue(TAG_CAMERA_PART_NUMBER, 16, null));
                    infoDirectory.setStringValue(TAG_CAMERA_SERIAL_NUMBER, reader3.getNullTerminatedStringValue(TAG_CAMERA_SERIAL_NUMBER, 16, null));
                    infoDirectory.setStringValue(TAG_CAMERA_SOFTWARE, reader3.getNullTerminatedStringValue(TAG_CAMERA_SOFTWARE, 16, null));

                    infoDirectory.setStringValue(TAG_LENS_MODEL, reader3.getNullTerminatedStringValue(TAG_LENS_MODEL, 32, null));
                    infoDirectory.setStringValue(TAG_LENS_PART_NUMBER, reader3.getNullTerminatedStringValue(TAG_LENS_PART_NUMBER, 16, null));
                    infoDirectory.setStringValue(TAG_LENS_SERIAL_NUMBER, reader3.getNullTerminatedStringValue(TAG_LENS_SERIAL_NUMBER, 16, null));

                    infoDirectory.setFloat(TAG_FIELD_OF_VIEW, reader3.getFloat32(TAG_FIELD_OF_VIEW));

                    infoDirectory.setStringValue(TAG_FILTER_MODEL, reader3.getNullTerminatedStringValue(TAG_FILTER_MODEL, 16, null));
                    infoDirectory.setStringValue(TAG_FILTER_PART_NUMBER, reader3.getNullTerminatedStringValue(TAG_FILTER_PART_NUMBER, 32, null));
                    infoDirectory.setStringValue(TAG_FILTER_SERIAL_NUMBER, reader3.getNullTerminatedStringValue(TAG_FILTER_SERIAL_NUMBER, 32, null));

                    infoDirectory.setInt(TAG_PLANCK_O, reader3.getInt32(TAG_PLANCK_O));
                    infoDirectory.setString(TAG_PLANCK_R2, new BigDecimal(reader3.getFloat32(TAG_PLANCK_R2)).toPlainString());

                    infoDirectory.setInt(TAG_RAW_VALUE_RANGE_MIN, reader3.getUInt16(TAG_RAW_VALUE_RANGE_MIN));
                    infoDirectory.setInt(TAG_RAW_VALUE_RANGE_MAX, reader3.getUInt16(TAG_RAW_VALUE_RANGE_MAX));

                    infoDirectory.setInt(TAG_RAW_VALUE_MEDIAN, reader3.getUInt16(TAG_RAW_VALUE_MEDIAN));
                    infoDirectory.setInt(TAG_RAW_VALUE_RANGE, reader3.getUInt16(TAG_RAW_VALUE_RANGE));

                    byte[] dateTimeBytes = new byte[10];
                    byte[] readBytes = reader3.getBytes(TAG_DATE_TIME_ORIGINAL, dateTimeBytes.length);
                    System.arraycopy(readBytes, 0, dateTimeBytes, 0, readBytes.length);
                    ByteBuffer buffer = ByteBuffer.wrap(dateTimeBytes);
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    long tm = Integer.toUnsignedLong(buffer.getInt());
                    int ss = buffer.getInt() & 0xFFFF;
                    short tz = buffer.getShort();
                    OffsetDateTime dateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(tm - tz * 60),
                        ZoneOffset.ofTotalSeconds(tz * 60)).plusSeconds(ss / 1000);
                    infoDirectory.setDate(TAG_DATE_TIME_ORIGINAL, Date.from(dateTime.toInstant()));

                    infoDirectory.setInt(TAG_FOCUS_STEP_COUNT, reader3.getUInt16(TAG_FOCUS_STEP_COUNT));
                    infoDirectory.setFloat(TAG_FOCUS_DISTANCE, reader3.getFloat32(TAG_FOCUS_DISTANCE));
                    infoDirectory.setInt(TAG_FRAME_RATE, reader3.getUInt16(TAG_FRAME_RATE));
                    metadata.addDirectory(infoDirectory);
                }
                indexOffset += 32;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Verify the beginning of the image
     *
     * @param data
     * @param prefix
     * @return
     */
    private static boolean flirTemperatureDataStartsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }

    /**
     * 转换为 int，处理溢出
     *
     * @param value
     * @return
     */
    private static int checkedToInt(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("Value is out of the range of int.");
        }
        return (int) value;
    }
}
