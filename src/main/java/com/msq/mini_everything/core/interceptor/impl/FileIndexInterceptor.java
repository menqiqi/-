package com.msq.mini_everything.core.Interceptor.impl;

import com.msq.mini_everything.core.Interceptor.FileInterceptor;
import com.msq.mini_everything.core.common.FileConvertThing;
import com.msq.mini_everything.core.dao.FileIndexDao;
import com.msq.mini_everything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor {

    private FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileConvertThing.convert(file);
        fileIndexDao.insert(thing);
    }
}
