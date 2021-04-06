package com.zk;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author yeeq
 * @date 2021/4/4
 */
public class QuickCheckEmailExtractor {

    private final ThreadPoolExecutor threadsPool;

    private final BlockingQueue<EmailDTO> emailQueue;

    private final EmailService emailService;

    public QuickCheckEmailExtractor() {
        emailQueue = new LinkedBlockingQueue<>();
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        threadsPool = new ThreadPoolExecutor(corePoolSize, corePoolSize, 101,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(2000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        emailService = new EmailService();
    }

    public void extract() {
        // 抽取所有邮件到队列里
        new ExtractEmailTask().start();
        // 处理队列里的邮件
        check();
    }

    private void check() {
        try {
            while (true) {
                // 两秒内取不到就退出
                EmailDTO email = emailQueue.poll(2, TimeUnit.SECONDS);
                if (email == null) {
                    break;
                }
                threadsPool.submit(new CheckEmailTask());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void extractEmail() {
        List<EmailDTO> allEmails = emailService.queryAllEmails();
        if (allEmails == null) {
            return;
        }
        for (EmailDTO emailDTO : allEmails) {
            emailQueue.offer(emailDTO);
        }
    }

    protected void checkEmail(EmailDTO email) {
        System.out.println("邮件" + email.getId() + "已处理");
    }

    public class ExtractEmailTask extends Thread {

        @Override
        public void run() {
            extractEmail();
        }

    }

    public class CheckEmailTask extends Thread {

        private EmailDTO email;

        @Override
        public void run() {
            checkEmail(email);
        }

        public CheckEmailTask() {
            super();
        }

        public CheckEmailTask(EmailDTO email) {
            super();
            this.email = email;
        }
    }
}
