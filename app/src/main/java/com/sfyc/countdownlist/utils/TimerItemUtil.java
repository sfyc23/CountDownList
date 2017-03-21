package com.sfyc.countdownlist.utils;

import com.sfyc.countdownlist.entity.TimerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Author :leilei on 2017/3/21 1422.
 */

public class TimerItemUtil {
    public static List<TimerItem> getTimerItemList() {
        List<TimerItem> lstTimerItems = new ArrayList<>();
        lstTimerItems.add(new TimerItem("A", System.currentTimeMillis() + 11 * 1000));
        lstTimerItems.add(new TimerItem("B", System.currentTimeMillis() + 22 * 1000));
        lstTimerItems.add(new TimerItem("C", System.currentTimeMillis() + 26 * 1000));
        lstTimerItems.add(new TimerItem("D", System.currentTimeMillis() + 33 * 1000));
        lstTimerItems.add(new TimerItem("E", System.currentTimeMillis() + 24 * 1000));
        lstTimerItems.add(new TimerItem("F", System.currentTimeMillis() + 98 * 1000));
        lstTimerItems.add(new TimerItem("G", System.currentTimeMillis() + 14 * 1000));
        lstTimerItems.add(new TimerItem("H", System.currentTimeMillis() + 36 * 1000));
        lstTimerItems.add(new TimerItem("I", System.currentTimeMillis() + 58 * 1000));
        lstTimerItems.add(new TimerItem("J", System.currentTimeMillis() + 47 * 1000));
        lstTimerItems.add(new TimerItem("K", System.currentTimeMillis() + 66 * 1000));
        lstTimerItems.add(new TimerItem("L", System.currentTimeMillis() + 55 * 1000));
        lstTimerItems.add(new TimerItem("M", System.currentTimeMillis() + 62 * 1000));
        lstTimerItems.add(new TimerItem("N", System.currentTimeMillis() + 45 * 1000));
        lstTimerItems.add(new TimerItem("O", System.currentTimeMillis() + 14 * 1000));

        return lstTimerItems;
    }
}
