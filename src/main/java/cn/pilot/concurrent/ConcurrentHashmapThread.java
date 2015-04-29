package cn.pilot.concurrent;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * poller polls a page from cloud, and save nextPageUrl into a hashmap
 */
public class ConcurrentHashmapThread implements Runnable {
    private String url;

    // test purpose
    private HashMap<String, String> urls = new HashMap<>();

    public ConcurrentHashmapThread(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            InMemory.urls.put(url, String.valueOf(i));
            urls.put(url, String.valueOf(i));
        }
    }

    public HashMap<String, String> getUrls() {
        return this.urls;
    }

    public static void main(String[] args) {
        new Thread(new ConcurrentHashmapThread("url01")).start();
        new Thread(new ConcurrentHashmapThread("url02")).start();
        new Thread(new ConcurrentHashmapThread("url03")).start();


        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(InMemory.urls);
    }
}

class InMemory{
    public final static HashMap<String, String> urls = new HashMap<>();
}