package cn.pilot.init;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WeirdTest {
    @Test
    public void test() throws Exception {
        // class variables
        assertTrue(Weird.INSTANCE.foo == 10);
        assertTrue(Weird.INSTANCE.bar == -10);
        assertTrue(Weird.INSTANCE.CLASS_FOO == 20);
        assertTrue(Weird.INSTANCE.CLASS_BAR == 20);

        assertTrue(Weird.CLASS_FOO == 20);
        assertTrue(Weird.CLASS_BAR == 20);

        // instance variables
        Weird weird = new Weird(10);
        assertTrue(weird.foo == 10);
        assertTrue(weird.bar == 10);
    }
}