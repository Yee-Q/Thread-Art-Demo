package com.zk;

/**
 * @author yeeq
 * @date 2021/3/25
 */
public class JoinCountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {

        Thread parser1 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("parser2 finish");
            }
        });

        Thread parser2 = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("parser2 finish");
            }
        });

        parser1.start();
        parser2.start();
        parser1.join();
        parser2.join();
        System.out.println("all parser finish");
    }
}
