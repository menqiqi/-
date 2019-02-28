package com.msq.mini_everything.core.Search.Impl;

import com.msq.mini_everything.core.Search.FileSearch;
import com.msq.mini_everything.core.dao.FileIndexDao;
import com.msq.mini_everything.core.model.Condition;
import com.msq.mini_everything.core.model.Thing;

import java.util.List;

/**
 * 业务层
 */
public class FileSearchImpl implements FileSearch {
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }


    @Override
    public List<Thing> search(Condition condition) {
        return this.fileIndexDao.search(condition);
    }
}
