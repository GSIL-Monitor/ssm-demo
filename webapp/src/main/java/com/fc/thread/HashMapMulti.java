package com.fc.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fangcong on 2018/3/16.
 */
public class HashMapMulti {

    static Map<String, String> map = new HashMap();

    public static class AddThread implements Runnable {

        int start = 0;

        public AddThread(int start) {
            this.start = start;
        }

        @Override
        public void run() {
            for (int i = start; i < 100000; i += 2) {
                map.put(Integer.toString(i), Integer.toBinaryString(i));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException{
        Thread t1 = new Thread(new HashMapMulti.AddThread(0));
        Thread t2 = new Thread(new HashMapMulti.AddThread(1));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("size = " + map.size());
    }
}
