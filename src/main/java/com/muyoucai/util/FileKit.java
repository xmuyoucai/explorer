package com.muyoucai.util;

import com.muyoucai.ex.CustomException;

import java.io.File;
import java.io.IOException;

/**
 * @author lzy
 */
public class FileKit {

    public static File createDir(String dirPath){
        File file = new File(dirPath);
        file.mkdir();
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
