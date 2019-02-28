package com.msq.mini_everything.core.index;

import com.msq.mini_everything.core.dao.DataSourceFactory;
import com.msq.mini_everything.core.dao.impl.FileIndexDaoImpl;
import com.msq.mini_everything.core.index.impl.FileScanImpl;
import com.msq.mini_everything.core.interceptor.FileInterceptor;
import com.msq.mini_everything.core.interceptor.impl.FileIndexInterceptor;
import com.msq.mini_everything.core.interceptor.impl.FilePrintInterceptor;

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

    public static void main(String[] args) {
        DataSourceFactory.initDatabase();

        FileScanImpl scan = new FileScanImpl();
        FileInterceptor printInterceptor = new FilePrintInterceptor();
        scan.interceptor(printInterceptor);
        FileInterceptor fileIndexInterceptor = new FileIndexInterceptor(
                new FileIndexDaoImpl(DataSourceFactory.dataSource()));
        scan.interceptor(fileIndexInterceptor);
        scan.index("E:\\课件");

    }
}
