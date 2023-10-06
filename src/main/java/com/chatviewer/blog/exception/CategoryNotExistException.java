package com.chatviewer.blog.exception;

/**
 * 当查找的分类目录不存在时，抛出该异常
 * @author ChatViewer
 */
public class CategoryNotExistException extends RuntimeException{
    public CategoryNotExistException() {
        super();
    }

    public CategoryNotExistException(String message) {
        super(message);
    }
}
