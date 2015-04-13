package cn.pilot.init;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InheritanceTest {
    @Test
    public void test() throws Exception {
        Child child = new Child();

        int weird = child.getWeird();

        assertTrue(child.getWeird() == 0);
        assertTrue(child.getVar() == 3);
        assertTrue(child.getParentVar() == 1);
        assertTrue(child.getFoo() == 100);
    }

    @Test
    public void a() throws Exception {
        PA pa = new CA();

        assertTrue(pa.var == 1);

        pa = (CA) pa;

        assertTrue(pa.var == 1); // TODO why?
        assertTrue(((CA) pa).var == 2);

        CA ca = (CA) pa;

        assertTrue(ca.var == 2);

        assertTrue(new CA().var == 2);
    }

    @Test
    public void b() throws Exception {
        PB pb = new CB();

        assertTrue(pb.getVar() == 2);
    }

    @Test
    public void c() throws Exception {
        PC pc = new CC();

        assertTrue(pc.method() == 1);
    }
}