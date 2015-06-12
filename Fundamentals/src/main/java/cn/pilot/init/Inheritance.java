package cn.pilot.init;

import lombok.Getter;

public class Inheritance {
    public static void main(String[] args) {

    }
}

// *************************************
class PA {
    public int var = 1;
}

class CA extends PA {
    public int var = 2;
}

// *************************************
@Getter
class PB {
    private int var = 1;
}

@Getter
class CB extends PB {
    private int var = 2;
}

// *************************************
class PC {
    private int var = 1;

    public int method() {
        return this.var;
    }
}

class CC extends PC {
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
class PE {
}

class CE extends PE {
}

// *************************************
@Getter
class PF {
    public int var = 1;
}

@Getter
class CF extends PF {
    public int var = 2;
}

// *************************************
class PG {
    public int var = 1;
    public int copy;

    public PG() {
        this.copy = var;
    }
}

class CG extends PG {
    public int var = 2;
    public int copy;

    public CG() {
        super();
    }
}