package cn.pilot.init;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InheritanceTest {

    @Test
    public void a() throws Exception {
        PA pa = new CA();

        assertTrue(pa.var == 1);

        pa = (CA) pa;
        // the type of pa is still PA
        // this line is equal to
        // pa = (PA) ((CA) pa) which means pa is assigned to itself

        assertTrue(pa.var == 1);
        assertTrue(((CA) pa).var == 2);

        CA ca = (CA) pa; // the type of ca is CA now

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

    @Test
    public void d() throws Exception {
        PD pd = new CD();

        assertTrue(pd.getVar() == 100);
        assertTrue(pd.getParentWeird() == 0);
        assertTrue(pd.method() == 200); // call child.method()

        assertTrue(((CD) pd).getChildWeird() == 200);
    }

    @Test
    public void e() throws Exception {
        PE pe = new CE();

        assertTrue(pe instanceof CE);
        assertTrue(pe instanceof PE);

        CE ce = new CE();
        assertTrue(ce instanceof CE);
        assertTrue(ce instanceof PE);
    }
}