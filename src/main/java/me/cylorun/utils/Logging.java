package me.cylorun.utils;

public class Logging {
    public static boolean showDebug = true;

    public static void error(Object o){
        System.err.println(o);
    }
    public static void warn(Object o){
        System.out.println(o);
    }
    public static void debug(Object o){
        System.out.println(o);
    }
    public static void info(Object o){
        System.out.println(o);
    }


}
