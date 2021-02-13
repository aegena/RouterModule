package com.campanula.router.module.log;


/**
 * 日志
 *
 * @author maweidong
 * date 2020-12-18
 */
public final class Log {

    private static final String prefix = "/CRouter";

    public static boolean debug = true;

    private static String getFormattedMessage(Color color, String message) {
        return (char) 27 + "[ " + color.getColor() + "m" + message + (char) 27 + " [0m";
    }

    public static void d(String message) {
        if (debug) {
            System.out.println(prefix + "   " + message);
        }
    }

    public static void i(String message) {
        System.out.println(getFormattedMessage(Color.GREEN, prefix + "   " + message));
    }

    public static void e(String message) {
        System.out.println(getFormattedMessage(Color.RED, prefix + "   " + message));
    }

    public static void w(String message) {
        System.out.println(getFormattedMessage(Color.YELLOW, prefix + "   " + message));

    }
}
