package cn.pilot.concurrent;

import cn.pilot.init.Inheritance;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertTrue;

public class SynchronizedKeywordTest {
    private SynchronizedKeyword demo = new SynchronizedKeyword();

    @Test
    public void synchronized_method_only_guarantee_this_method_is_only_executed_by_a_single_thread() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    demo.changeVar();
                }
            }
        });

        Future<Integer> result = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return demo.get();
            }
        });


        while (true) {
            if (result.isDone()) {
                assertTrue(result.get() != 200);
                executor.shutdown();
                return;
            }
        }
    }

    @Test
    public void synchronized_only_guarantee_block_is_only_executed_by_a_single_thread() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    demo.changeVar();
                }
            }
        });

        Future<Integer> result = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return demo.take();
            }
        });


        while (true) {
            if (result.isDone()) {
                assertTrue(result.get() != 200);
                executor.shutdown();
                return;
            }
        }
    }
}