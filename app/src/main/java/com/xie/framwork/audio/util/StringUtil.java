package com.xie.framwork.audio.util;

/**
 * Created by chenxf on 17-7-14.
 */

public class StringUtil {
    /**
     * 格式化时间展示为05’10”
     */
    public static String formatRecordTime(long recTime, long maxRecordTime) {
        int time = (int) ((maxRecordTime - recTime) / 1000);
        int minute = time / 60;
        int second = time % 60;
        //return String.format("%2d’%2d”", minute, second);
        return String.format("%2d:%2d", minute, second);
    }


    public static String formatTime(int recTime) {
        int minute = recTime / 60;
        int second = recTime % 60;
        return String.format("%2d’%2d”", minute, second);
    }

    public static String formatTimeLong(long recTime) {
        long timeInMin = recTime / 1000;
        int minute = (int) (timeInMin / 60);
        int second = (int) (timeInMin % 60);
        return String.format("%02d:%02d", minute, second);
    }


}
