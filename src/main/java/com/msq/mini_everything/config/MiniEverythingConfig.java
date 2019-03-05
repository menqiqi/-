package com.msq.mini_everything.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter
@ToString
public class MiniEverythingConfig {

    private static volatile MiniEverythingConfig config;

    /**
     * 建立索引的路径
     */
    private Set<String> includePath = new HashSet<>();

    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>();

    /**
     * 检索最大的返回值数量
     */
    @Setter
    private Integer maxReturn = 25;

    /**
     * 深度排序，默认升序
     */
    @Setter
    private Boolean deptOrder = true;

    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "mini_everything";


    private MiniEverythingConfig(){

    }

    private void initDefaultPathsConfig(){
        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(path -> config.includePath.add(path.toString()));

        //排除的目录
        //windows:C:\Windows C:\ProgramFiles(X86) C:\ProgramFiles C:\ProgramData
        //linux: /tmp /etc
        String osname = System.getProperty("os.name");
        if (osname.startsWith("Windows")){
            //windos系统
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files(x86)");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add(" C:\\ProgramData");
        }else {
            //linux系统
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }

    }

    public static MiniEverythingConfig getInstance(){
        if (config == null){
            synchronized (MiniEverythingConfig.class){
                if (config == null){
                    config = new MiniEverythingConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }

}
