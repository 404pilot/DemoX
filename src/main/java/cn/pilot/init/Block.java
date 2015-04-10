package cn.pilot.init;

import lombok.Getter;

import static cn.pilot.init.helper.Helper.assign;

@Getter
public class Block {
    {
        // Step 1
        var = assign(1);

        obj = "1";
        // obj.length() --> illegal forward reference (only assignment is allowed)
    }

    private String obj = "2";

    // Step2
    private int var = assign(2);

    public Block() {
        // Step 4
        this.var = assign(4);
    }

    {
        // Step 3
        var = assign(3);
    }
}