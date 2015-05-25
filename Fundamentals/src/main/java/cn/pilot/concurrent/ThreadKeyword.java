package cn.pilot.concurrent;


public class ThreadKeyword {

    public void neverEnds() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                }
            }
        }).start();
    }

    public String waitAtJoin(final int a, final int b) {
        // not thread-safe
        final StringBuilder sb = new StringBuilder();
        Thread first = new Thread(new Runnable() {
            @Override
            public void run() {
                sb.append(a);
            }
        });

        Thread second = new Thread(new Runnable() {
            @Override
            public void run() {
                sb.append(b);
            }
        });


        try {
            first.start();

            // wait for first thread to be ended
            first.join();

            second.start();

            second.join();

            return sb.toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

}