package com.muyoucai.util;

import com.muyoucai.util.ex.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * @author lzy
 */
@Slf4j
public class FileKit {

    public static boolean exists(String filepath){
        return new File(filepath).exists();
    }

    public static File createDir(String dirPath){
        File dir;
        (dir = new File(dirPath)).mkdirs();
        log.info("create dir : {}", dir);
        return dir;
    }

    public static File safelyCreateDir(String dirPath){
        File dir;
        if((dir = new File(dirPath)).exists()){
            return dir;
        }
        return createDir(dirPath);
    }

    public static File createFile(String filepath){
        File file;
        try {
            (file = new File(filepath)).createNewFile();
            log.info("create file : {}", filepath);
            return file;
        } catch (IOException e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static File safelyCreateFile(String filepath){
        File file;
        if((file = new File(filepath)).exists()){
            return file;
        }
        return createFile(filepath);
    }

}
