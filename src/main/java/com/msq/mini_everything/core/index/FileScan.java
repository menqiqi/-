package com.msq.mini_everything.core.index;

import com.msq.mini_everything.core.Interceptor.FileInterceptor;

public interface FileScan {

    /**
     * 遍历path
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);
}
