package cn.pilot.init;

import org.junit.Test;

import static org.junit.Assert.*;

public class InheritanceTest {
    @Test
    public void test() throws Exception {
        Child child = new Child();

        int weird = child.getWeird();

        assertTrue(child.getWeird() == 0);
        assertTrue(child.getVar() == 3);
        assertTrue(child.getParentVar() == 1);
    }
}