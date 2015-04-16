package cn.pilot.concurrent;

import java.util.concurrent.TimeUnit;

public class SynchronizedKeyword {
    int var;

    public void changeVar() {
        this.var = 100;
    }

    public synchronized int get() {
        this.var = 200;

        try {
            // test purpose
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this.var;
    }

    public int take() {
        synchronized (this) {
            try {
                // test purpose
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return this.var;
        }
    }
}