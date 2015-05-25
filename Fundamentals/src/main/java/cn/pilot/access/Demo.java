package cn.pilot.access;

import cn.pilot.access.upper.Access;

public class Demo {
    public static void main(String[] args) {
        Access access = new Access();

        // not allowed:
        //access.aDefault = 1;
        //access.aProtected = 2;
    }
}