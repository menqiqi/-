package com.msq.mini_everything.core.monitor;

import com.msq.mini_everything.core.common.HandlePath;

public interface FileWatch {

    /**
     * 监听启动
     */
    void start();

    /**
     * 监听的目录
     */
    void monitor(HandlePath handlePath);

    /**
     * 监听停止
     */
    void stop();
}
