package com.campanula.router.module.util;

/**
 * 文本处理器
 *
 * @author maweidong
 * date 2020-12-18
 */
public final class Text {
    /**
     * 文本是null 或者 长度为0
     *
     * @param text
     * @return
     */
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static String syncString(String newString, String syncString) {
        if (isEmpty(newString)) return syncString;
        return newString;
    }
}
