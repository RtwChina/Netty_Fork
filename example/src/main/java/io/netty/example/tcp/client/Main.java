package io.netty.example.tcp.client;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author rtw
 * @since 2019-06-15
 */
public class Main {
    public static void main(String[] args) {
        Executor executor = Executors.newFixedThreadPool(2);
        executor.execute(()->{
            while (true) {
                System.out.println("发送你好");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
