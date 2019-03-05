package com.msq.mini_everything.core.Interceptor;

import java.io.File;

@FunctionalInterface
public interface FileInterceptor {

    void apply(File file);
}
