package com.chatviewer.blog.service;

import java.util.Date;

/**
 * @author ChatViewer
 */
public interface DataService {

    /**
     * 将IP计入UV
     * @param ip IP地址
     */
    void recordIp(String ip);


    /**
     * 计算start到end之间的UV
     * @param start 开始日期
     * @param end 结束日期
     * @return 统计[start, end]时间内的UV
     */
    Long calIntervalUv(Date start, Date end);
}
