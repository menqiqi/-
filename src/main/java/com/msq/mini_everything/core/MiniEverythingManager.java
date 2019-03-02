package com.msq.mini_everything.core;

import com.msq.mini_everything.config.MiniEverythingConfig;
import com.msq.mini_everything.core.Search.FileSearch;
import com.msq.mini_everything.core.Search.Impl.FileSearchImpl;
import com.msq.mini_everything.core.dao.DataSourceFactory;
import com.msq.mini_everything.core.dao.FileIndexDao;
import com.msq.mini_everything.core.dao.impl.FileIndexDaoImpl;
import com.msq.mini_everything.core.index.FileScan;
import com.msq.mini_everything.core.index.impl.FileScanImpl;
import com.msq.mini_everything.core.interceptor.impl.FileIndexInterceptor;
import com.msq.mini_everything.core.interceptor.impl.ThingClearInterceptor;
import com.msq.mini_everything.core.model.Condition;
import com.msq.mini_everything.core.model.Thing;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MiniEverythingManager {

    private static MiniEverythingManager manager;

    private FileSearch fileSearch;

    private FileScan fileScan;

    private ExecutorService executorService;

    /**
     * 清理删除的文件
     */
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);


    public MiniEverythingManager() {
        this.initComponent();
    }

    private void initComponent(){
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

        /**
         * 检查数据库
         */
        checkDatabase();

        //业务层对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
        //this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));

        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true);
    }

    private void checkDatabase(){
        String fileName = MiniEverythingConfig.getInstance().getH2IndexPath() + File.separator + ".mv.db";
        File dbFile = new File(fileName);
        if (dbFile.isFile() && !dbFile.exists()){
            DataSourceFactory.initDatabase();
        }
    }

    public static MiniEverythingManager getInstance(){
        if (manager == null){
            synchronized (MiniEverythingManager.class){
                if (manager == null){
                    manager = new MiniEverythingManager();
                }
            }
        }
        return manager;
    }

    /**
     * 检索
     */
    public List<Thing> search(Condition condition){
        return this.fileSearch.search(condition).stream().filter(thing -> {
            String path = thing.getPath();
            File f = new File(path);
            boolean flag = f.exists();
            if (!flag){
                //删除
                thingClearInterceptor.apply(thing);
            }

            return flag;
        }).collect(Collectors.toList());
    }

    /**
     * 索引
     */
    public void buildIndex(){
        Set<String> directories = MiniEverythingConfig.getInstance().getIncludePath();

        if (this.executorService == null){
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-"+threadId.getAndIncrement());
                    return thread;
                }
            });
        }

        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());

        System.out.println("Build index start ...");
        for (String path : directories){
           this.executorService.submit(new Runnable() {
               @Override
               public void run() {
                   MiniEverythingManager.this.fileScan.index(path);
                   //当前任务完成，值-1
                   countDownLatch.countDown();
               }
           });
        }


        //阻塞，直到任务完成，值为0
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index complete ...");
    }

    /**
     * 启动清理线程
     */
    public void startBackgroundClearThread(){
        if (this.backgroundClearThreadStatus.compareAndSet(false, true)){
            this.backgroundClearThread.start();
        }else {
            System.out.println("Cant repeat start BackgroundClearThread");
        }
    }
}
