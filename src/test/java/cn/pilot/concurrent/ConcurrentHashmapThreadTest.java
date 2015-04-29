package cn.pilot.concurrent;

import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;

public class ConcurrentHashmapThreadTest {

    @Test
    public void outsideHashmap() throws Exception {
        String url01 = "url01";
        String url02 = "url02";
        String url03 = "url03";

        ConcurrentHashmapThread poller01 = new ConcurrentHashmapThread(url01);
        ConcurrentHashmapThread poller02 = new ConcurrentHashmapThread(url02);
        ConcurrentHashmapThread poller03 = new ConcurrentHashmapThread(url03);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<?> result01 = executor.submit(poller01);
        Future<?> result02 = executor.submit(poller02);
        Future<?> result03 = executor.submit(poller03);


        while (result01.isDone() && result02.isDone() && result03.isDone()) {
            HashMap<String, String> urls01 = poller01.getUrls();
            HashMap<String, String> urls02 = poller02.getUrls();
            HashMap<String, String> urls03 = poller03.getUrls();

            // url is different
            assertTrue(urls01 != urls02);
            assertTrue(urls01 != urls03);
            assertTrue(urls02 != urls03);

            // then hashmap is accessed by a single thread
            assertTrue(urls01.size() == 1);
            assertTrue(urls02.size() == 1);
            assertTrue(urls03.size() == 1);

            assertTrue(urls01.get(url01).equals("99"));
            assertTrue(urls02.get(url02).equals("99"));
            assertTrue(urls03.get(url03).equals("99"));

            // shared hashmap
            HashMap<String, String> urls = InMemory.urls;

            assertTrue(urls.size() == 3);
            assertTrue(urls.get(url01).equals("99"));
            assertTrue(urls.get(url02).equals("99"));
            assertTrue(urls.get(url03).equals("99"));
        }
    }
}