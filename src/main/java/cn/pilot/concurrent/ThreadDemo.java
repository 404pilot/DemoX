package cn.pilot.concurrent;

public class ThreadDemo implements Runnable {

    private String name;
    private Object prev;
    private Object self;

    private ThreadDemo(String name, Object prev, Object self) {
        this.name = name;
        this.prev = prev;
        this.self = self;
    }

    @Override
    public void run() {
        int count = 10;
        while (count > 0) {
            synchronized (prev) {
                synchronized (self) {
                    System.out.print(name);
                    count--;

                    self.notify();
                }
                try {
                    prev.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void main(String[] args) throws Exception {
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        ThreadDemo pa = new ThreadDemo("A", c, a);
        ThreadDemo pb = new ThreadDemo("B", a, b);
        ThreadDemo pc = new ThreadDemo("C", b, c);


        new Thread(pa).start();
        new Thread(pb).start();
        new Thread(pc).start();
    }
}