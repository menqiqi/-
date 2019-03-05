package com.msq.mini_everything.core.index.impl;

import com.msq.mini_everything.config.MiniEverythingConfig;
import com.msq.mini_everything.core.Interceptor.FileInterceptor;
import com.msq.mini_everything.core.index.FileScan;

import java.io.File;
import java.util.LinkedList;

public class FileScanImpl implements FileScan {

    private MiniEverythingConfig config = MiniEverythingConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);

        if (file.isFile()) {
            if (config.getExcludePath().contains(file.getParent())) {
                return;
            }
        } else {
            //目录
            if (config.getExcludePath().contains(path)) {
                return;
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
        }

        //File Directory
        for (FileInterceptor interceptor : this.interceptors){
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}

