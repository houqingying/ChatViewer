package com.chatviewer.blog.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ChatViewer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code = 200;
    private String msg = "success";
    private T data;

    public static Result<Object> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 默认错误消息
     * @return 返回错误代码 404 not found
     */
    public static Result<Object> fail() {
        return new Result<>(404, "fail", null);
    }

    public static Result<Object> fail(String msg) {
        return new Result<>(404, msg, null);
    }

    public static Result<Object> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

}
