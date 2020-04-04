package com.muyoucai.util;

import com.muyoucai.util.ex.CustomException;

import java.io.File;
import java.io.IOException;

/**
 * @author lzy
 */
public class FileKit {

    public static boolean exists(String filepath){
        return new File(filepath).exists();
    }

    public static File openOrCreateDir(String dirPath){
        File file = new File(dirPath);
        if(!file.exists()){
            file.mkdir();
        }
        return file;
    }

    public static File openOrCreateFile(String filepath){
        File file = new File(filepath);
        try {
            if(file.exists()){
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new CustomException(e);
        }
    }

}
