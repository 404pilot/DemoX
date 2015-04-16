package cn.pilot.concurrent;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThreadKeywordTest {
    private ThreadKeyword demo = new ThreadKeyword();

    @Test
    public void junit_call_system_exit() throws Exception {
        demo.neverEnds();
    }

    @Test
    public void current_thread_waits_when_join_is_called() throws Exception {
        String result = demo.waitAtJoin(1, 2);

        assertTrue(result.equals("12"));
    }
}