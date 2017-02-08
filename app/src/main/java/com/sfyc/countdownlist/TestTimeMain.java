package com.sfyc.countdownlist;

import com.sfyc.countdownlist.utils.TimeTools;

/**
 * Author :leilei on 2016/11/11 2355.
 */
public class TestTimeMain {

    public static void main(String[] arg){
        long time = 3 * 24 * 59 * 59 * 1000;
        String count = TimeTools.getCountTimeByLong(time);
        System.out.println(count);
    }
}
