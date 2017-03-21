package com.sfyc.countdownlist.entity;

/**
 * Author :leilei on 2017/3/21 1420.
 */
public class TimerItem {
    //其他属性
    public String name;
    //倒计时长，单位毫秒
    public long expirationTime;

    public TimerItem(String name, long expirationTime) {
        this.name = name;
        this.expirationTime = expirationTime;
    }
}
