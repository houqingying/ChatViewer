package com.chatviewer.blog.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类，用于检查某些参数的格式输入是否正确，避免非法请求到达数据库
 * @author ChatViewer
 */
public class FormatChecker {

    private static final String PHONE_PATTERN = "^(?:(?:\\+|00)86)?1(?:(3[\\d])|(4[5-79])|(5[0-35-9])|(6[5-7])|(7[0-8])|(8[\\d])|(9[189]))\\d{8}$";
    private static final String CODE_PATTERN = "\\d{4}";

    /**
     * @param str 字符串
     * @return str是否为手机号
     */
    public static boolean isPhone(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * @param str 字符串
     * @return str是否为验证码
     */
    public static boolean isCode(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(CODE_PATTERN);
        Matcher match = pattern.matcher(str);
        return match.matches();
    }
}
