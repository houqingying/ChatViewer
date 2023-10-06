package com.chatviewer.blog.exception;

/**
 * 当用户试图更改其他用户信息、或进行无权限操作时抛出该异常
 * @author ChatViewer
 */
public class NoPermissionException extends RuntimeException{
    public NoPermissionException() {
        super();
    }

    public NoPermissionException(String message) {
        super(message);
    }
}
