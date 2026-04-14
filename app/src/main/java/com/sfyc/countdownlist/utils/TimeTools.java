package com.sfyc.countdownlist.utils;

/**
 * Author :leilei on 2016/11/11 2326.
 */
public class TimeTools {

    /**
     * 毫秒转换为 HH:MM:SS 格式字符串
     */
    public static String getCountTimeByLong(long finishTime) {
        long totalTime = finishTime / 1000;
        long hour = totalTime / 3600;
        long minute = (totalTime % 3600) / 60;
        long second = totalTime % 60;

        StringBuilder sb = new StringBuilder();
        if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (minute < 10) {
            sb.append("0").append(minute).append(":");
        } else {
            sb.append(minute).append(":");
        }
        if (second < 10) {
            sb.append("0").append(second);
        } else {
            sb.append(second);
        }
        return sb.toString();
    }

}
