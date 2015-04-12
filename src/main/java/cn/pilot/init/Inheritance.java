package cn.pilot.init;

public class Inheritance {
    public static void main(String[] args) {
        Parent child = new Child();

        System.out.printf("child.weird():\t%d\n", child.getWeird());

        System.out.printf("child.var:\t%d\n", child.getVar());

        System.out.printf("child.parentVar:\t%d\n",child.getParentVar());
    }
}

class Parent {
    private int var = 1;

    private int weird = 1;

    private int parentVar;

    public Parent() {
        this.parentVar = this.weird;
        this.weird = this.weird();
    }

    public int weird() {
        return this.var;
    }

    public int getWeird() {
        return this.weird;
    }

    public int getVar() {
        return this.var;
    }

    public int getParentVar() {
        return this.parentVar;
    }
}

class Child extends Parent {
    private int var = 2;

    public Child(){
        this.var = 3;
    }

    @Override
    public int weird() {
        return this.var;
    }

    @Override
    public int getVar() {
        return this.var;
    }
}