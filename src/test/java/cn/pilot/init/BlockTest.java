package cn.pilot.init;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BlockTest {
    @Test
    public void init_is_correct() throws Exception {
        assertTrue(new Block().getVar() == 4);
    }
}