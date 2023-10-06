package com.chatviewer.blog.exception;

/**
 * 当用户账户余额不足，仍调用ChatGPT时，抛出该异常
 * @author ChatViewer
 */
public class NoTokenCountException extends RuntimeException{
    public NoTokenCountException() {
        super();
    }

    public NoTokenCountException(String message) {
        super(message);
    }
}
