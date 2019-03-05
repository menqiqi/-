package com.msq.mini_everything.core.common;

import com.msq.mini_everything.core.model.FileType;
import com.msq.mini_everything.core.model.Thing;

import java.io.File;

public final class FileConvertThing {
    private FileConvertThing(){}

    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
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
        if (index != -1 && index < fileName.length() - 1){
            String extend = fileName.substring(index + 1);
            return FileType.lookup(extend);
        }
        return FileType.OTHER;
    }
}
