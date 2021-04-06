package com.zk;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * @author yeeq
 * @date 2021/2/24
 */
public class ConnectionPool {

    private final LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize) {
        // 初始化连接的最大上限
        if (initialSize > 0) {
            for (int i = 0; i < initialSize; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                /* 连接释放后需要进行通知
                 * 这样其他消费者就能知道连接池已经归还了一个连接
                 */
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    /**
     * 在给定毫秒时间内获取连接
     */
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            // 完全超时
            if (mills < 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}
