package com.zk.util;

import java.util.concurrent.TimeUnit;

/**
 * @author yeeq
 * @date 2021/2/19
 */
public class SleepUtils {

    public static final void second(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
