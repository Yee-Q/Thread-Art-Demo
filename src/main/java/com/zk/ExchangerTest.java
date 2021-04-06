package com.zk;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yeeq
 * @date 2021/3/25
 */
public class ExchangerTest {

    private static final Exchanger<String> exchanger = new Exchanger<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {

        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    // A录入银行流水数据
                    String A = "银行流水A";
                    exchanger.exchange(A);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    // B录入银行流水数据
                    String B = "银行流水B";
                    String A = exchanger.exchange(B);
                    System.out.println("A 和 B 数据是否一致：" + A.equals(B) + ", A 录入的是：" + A
                        + ", B 录入的是：" + B);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        threadPool.shutdown();
    }
}
