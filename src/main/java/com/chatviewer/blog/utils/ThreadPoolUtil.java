package com.chatviewer.blog.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author ChatViewer
 */
public class ThreadPoolUtil {

    public static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNamePrefix("thread-pool").build();

    public static ExecutorService pool = new ThreadPoolExecutor(5, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
}
