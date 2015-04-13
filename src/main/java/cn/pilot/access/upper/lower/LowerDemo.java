package cn.pilot.access.upper.lower;

import cn.pilot.access.upper.Access;

public class LowerDemo {
    public static void main(String[] args) {
        Access access = new Access();

        //not allowed:
        //access.aDefault = 1;
        //access.aProtected = 2;
    }
}