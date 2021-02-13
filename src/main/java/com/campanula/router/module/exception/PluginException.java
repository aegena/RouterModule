package com.campanula.router.module.exception;

/**
 * 寻址插件，异常class
 * @author maweidong
 * date 2020-12-18
 */
public class PluginException extends Exception {
    public PluginException() {
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
