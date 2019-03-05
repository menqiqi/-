package com.msq.mini_everything.core.Interceptor.impl;

import com.msq.mini_everything.core.Interceptor.ThingInterceptor;
import com.msq.mini_everything.core.dao.FileIndexDao;
import com.msq.mini_everything.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingClearInterceptor implements ThingInterceptor, Runnable {

    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }

    @Override
    public void run() {
        while (true){
            Thing thing = this.queue.poll();
            if (thing != null){
                fileIndexDao.delete(thing);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
