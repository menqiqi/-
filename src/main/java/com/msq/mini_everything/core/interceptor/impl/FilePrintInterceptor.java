package com.msq.mini_everything.core.Interceptor.impl;

import com.msq.mini_everything.core.Interceptor.FileInterceptor;

import java.io.File;

public class FilePrintInterceptor implements FileInterceptor {
    @Override
    public void apply(File file) {
        System.out.println(file.getAbsolutePath());
    }
}
