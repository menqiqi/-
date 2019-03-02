package com.msq.mini_everything.core.common;

import com.msq.mini_everything.core.model.FileType;
import com.msq.mini_everything.core.model.Thing;

import java.io.File;


/**
 * 辅助工具类，将File对象转化为Thing对象
 */
public final class FileConvertThing {

    private FileConvertThing(){}

    public static Thing covert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getPath());
        thing.setDepth(computerFileDepth(file));
        thing.setFileType(computerFileType(file));
        return thing;
    }

    private static int computerFileDepth(File file){
        String[] segments = file.getAbsolutePath().split("\\\\");
        return segments.length;
    }

    private static FileType computerFileType(File file){
        if (file.isDirectory()){
            return FileType.OTHER;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        //防止数组越界，if index=length-1，会发生越界
        if (index != -1 && index < fileName.length() - 1){
            String extend = file.getName().substring(index + 1);
            return FileType.lookup(extend);
        }else {
            return FileType.OTHER;
        }
    }
}
