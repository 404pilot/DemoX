package cn.pilot.concurrent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.*;

public class ThreadDemoTest {
    @Test
    public void callable_canThrowCheckedException() throws Exception {
        Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                throw new CustomException("checked exception can be thrown");
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(1);

        Future<Void> result = service.submit(callable);

        try {
            result.get();
        } catch (InterruptedException e) {
            fail("impossible to get here");
        } catch (ExecutionException e) {
            assertTrue("checked exception can be caught here", e.getCause() instanceof CustomException);
        }

        service.shutdown();
    }

    @Test
    public void runnable_cannotThrowCheckedException() throws Exception {
        Runnable runnbale = new Runnable() {
            @Override
            public void run() {
                try {
                    throw new CustomException("");
                } catch (CustomException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private class CustomException extends Exception {
        public CustomException(String message) {
            super(message);
        }
    }

    @Test
    public void shutdown_bestPractice() {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        pool.shutdown(); // Disable new tasks from being submitted

        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void shutdown_waitsForEndOfCurrentThread() {
        final String END_MESSAGE = "end";
        ExecutorService pool = Executors.newFixedThreadPool(1);

        Future<String> result = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(3);

                return END_MESSAGE;
            }
        });

        pool.shutdown(); // initiate a shutdown immediately

        String actual = null;
        try {
            actual = result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertThat("shutdown() will wait for thread to be finished", actual, equalTo(END_MESSAGE));
    }

    @Test
    public void shutdownNow_willShutdownImmediately() {
        final String END_MESSAGE = "end";
        ExecutorService pool = Executors.newFixedThreadPool(1);

        Future<String> result = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(3);

                return END_MESSAGE;
            }
        });

        pool.shutdownNow(); // initiate a shutdown immediately

        String actual = null;

        try {
            actual = result.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
            assertThat("thread's sleep will be interrupted and throw exception", e.getCause(), instanceOf(InterruptedException.class));
        }

        assertThat("actual should be null", actual, nullValue());
    }

    @Test
    public void awaitTermination_canBeDoneWithinTimeout() {
        ExecutorService pool = Executors.newFixedThreadPool(1);

        Future<Void> result = pool.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        });

        long start = System.currentTimeMillis();

        pool.shutdown(); // initiate a shutdown immediately

        try {
            pool.awaitTermination(100, TimeUnit.SECONDS);

            long end = System.currentTimeMillis();

            assertThat("timeout is a max value", (int) (end - start), lessThan(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void scheduleAtFixedRate_periodIsFixed() throws Exception {
        final ArrayList<Long> times = new ArrayList<>();

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                times.add(System.currentTimeMillis());
                System.out.println(new Date());
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        pool.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);

        TimeUnit.MILLISECONDS.sleep(2500);

        pool.shutdown();

        // 0s -> start
        // 1s -> start? no, previous task is ended
        // 2s -> end
        // 2s -> start
        assertThat("only twice", times.size(), equalTo(2));
    }

    @Test
    public void scheduleWithFixedDelay_delayIsFixed() throws Exception {
        final ArrayList<Long> times = new ArrayList<>();

        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                times.add(System.currentTimeMillis());
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        pool.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);

        TimeUnit.MILLISECONDS.sleep(2500);

        pool.shutdown();

        // 0s -> start
        // 2s -> end
        // delay 1s
        // 3s -> start
        assertThat("only twice", times.size(), equalTo(1));
    }
}