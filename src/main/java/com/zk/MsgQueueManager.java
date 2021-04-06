package com.zk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author yeeq
 * @date 2021/4/4
 */
public class MsgQueueManager {

    /**
     * 消息总队列
     */
    private final BlockingQueue<Message> messageQueue;

    /**
     * 消息子队列集合
     */
    private final List<BlockingQueue<Message>> subMsgQueues;

    private MsgQueueManager() {
        messageQueue = new LinkedBlockingQueue<>();
        subMsgQueues = new ArrayList<>();
    }

    public static MsgQueueManager getInstance() {
        return new MsgQueueManager();
    }

    public void put(Message msg) {
        try {
            messageQueue.put(msg);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Message take() {
        try {
            return messageQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * 消费者线程获取子队列
     */
    public BlockingQueue<Message> addSubMsgQueue() {
        BlockingQueue<Message> subMsgQueue = new LinkedBlockingQueue<>();
        subMsgQueues.add(subMsgQueue);
        return subMsgQueue;
    }

    /**
     * 消息分发线程，负责把消息从大队列塞到小队列里
     */
    class DispatchMessageTask implements Runnable {

        /**
         * 控制消息分发开始与结束
         */
        private boolean flag = true;

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run() {
            BlockingQueue<Message> subQueue;
            while (flag) {
                // 如果没有数据，则阻塞在这里
                Message msg = take();
                // 如果为空，表示没有Session连接，需要等待Session连接上来
                while ((subQueue = getSubQueue()) == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                // 把消息放到小队列里
                try {
                    subQueue.put(msg);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        /**
         * 均衡获取一个子队列
         */
        public BlockingQueue<Message> getSubQueue() {
            List<BlockingQueue<Message>> subMsgQueues = getInstance().subMsgQueues;
            if (subMsgQueues.isEmpty()) {
                return null;
            }
            int index = (int) (System.nanoTime() % subMsgQueues.size());
            return subMsgQueues.get(index);
        }
    }
}
