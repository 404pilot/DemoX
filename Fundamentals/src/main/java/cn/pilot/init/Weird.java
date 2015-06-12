package cn.pilot.init;

public class Weird {
    static int CLASS_FOO = 20;
    final static Weird INSTANCE = new Weird(10);
    static int CLASS_BAR = 20;

    int foo;
    int bar;

    public Weird(int i) {
        foo = CLASS_FOO - i;
        bar = CLASS_BAR - i;
    }

    public static void main(String[] args) {
        System.out.printf("Weird.INSTANCE.CLASS_FOO:\t%d\n", Weird.INSTANCE.CLASS_FOO);
        System.out.printf("Weird.INSTANCE.CLASS_BAR:\t%d\n", Weird.INSTANCE.CLASS_BAR);
        System.out.printf("Weird.INSTANCE.foo:\t\t\t%d\n", Weird.INSTANCE.foo);
        System.out.printf("Weird.INSTANCE.bar:\t\t\t%d\n", Weird.INSTANCE.bar);

        Weird weird = new Weird(10);
        System.out.printf("new Weird(10).foo:\t\t\t%d\n", weird.foo);
        System.out.printf("new Weird(10).bar:\t\t\t%d\n", weird.bar);
    }
}