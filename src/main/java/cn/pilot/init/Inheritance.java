package cn.pilot.init;

public class Inheritance {
    public static void main(String[] args) {

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
class PD {
    private int weird = 100;

    private int var = 1;

    public PD() {
        this.var = this.weird;
        this.weird = this.method();
    }

    public int method() {
        return this.weird;
    }

    public int getParentWeird() {
        return this.weird;
    }

    public int getVar() {
        return this.var;
    }
}

class CD extends PD {
    private int weird = 200;

    @Override
    public int method() {
        return this.weird;
    }

    public int getChildWeird() {
        return this.weird;
    }
}

// *************************************
class PE{
}

class CE extends PE{
}