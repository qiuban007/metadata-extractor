package com.drew.metadata;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.flir.FlirCameraInfoDirectory;
import com.drew.metadata.flir.FlirRawDataDirectory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class aaa {
    public static void main(String[] args) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(new File("E:\\1.jpg"));
        FlirRawDataDirectory flirRawDataDirectory = metadata.getFirstDirectoryOfType(FlirRawDataDirectory.class);
        byte[] byteArray = flirRawDataDirectory.getByteArray(FlirRawDataDirectory.TAG_RAW_THERMAL_IMAGE);
        ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        int anInt = exifIFD0Directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
        FlirCameraInfoDirectory flirCameraInfoDirectory = metadata.getFirstDirectoryOfType(FlirCameraInfoDirectory.class);
        // 遍历并输出 FlirCameraInfoDirectory 中的所有标签
        //for (Tag tag : flirCameraInfoDirectory.getTags()) {
        //    System.out.println(tag.getTagName() + " : " + tag.getDescription());
        //}
        for (Directory directory : metadata.getDirectories()) {
            // 遍历目录中的所有标签
            for (Tag tag : directory.getTags()) {
                // 输出标签的描述信息
                System.out.println(tag.getTagName() + " : " + tag.getDescription());
            }
            // 如果存在错误，则输出错误信息
            //if (directory.hasErrors()) {
            //    for (String error : directory.getErrors()) {
            //        System.err.println("ERROR: " + error);
            //    }
            //}
        }

        int bits = 1065353216;
        float f = Float.intBitsToFloat(bits);
        System.out.println("f=" + f);
        BigDecimal decimal = new BigDecimal(f);
        System.out.println("decimal=" + decimal.toPlainString());

    }
}
