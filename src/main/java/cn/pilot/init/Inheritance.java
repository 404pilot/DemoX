package cn.pilot.init;

public class Inheritance {
    public static void main(String[] args) {
        Parent child = new Child();

        System.out.printf("child.weird():\t%d\n", child.getWeird());

        System.out.printf("child.var:\t%d\n", child.getVar());

        System.out.printf("child.parentVar:\t%d\n", child.getParentVar());

        System.out.printf("child.getFoo():\t%d\n", child.getFoo());
    }
}
// *************************************
class PA{
    int var = 1;
}

class CA extends PA {
    int var = 2;
}

// *************************************
class PB{
    private int var = 1;

    public int getVar() {
        return this.var;
    }
}

class CB extends PB {
    private int var = 2;

    @Override
    public int getVar() {
        return this.var;
    }
}

// *************************************
class PC{
    private int var = 1;

    public int method() {
        return this.var;
    }
}

class CC extends  PC{
    private int var = 2;

    @Override
    public int method() {
        return super.method();
    }
}

// *************************************


class Parent {
    private int var = 1;

    private int weird = 1;

    private int parentVar;

    private int foo = 100;

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

    public int getFoo() {
        return this.foo;
    }
}

class Child extends Parent {
    private int var = 2;

    private int parentVar = 200;

    private int foo = 300;

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

    @Override
    public int getFoo() {
        return super.getFoo();
    }
}