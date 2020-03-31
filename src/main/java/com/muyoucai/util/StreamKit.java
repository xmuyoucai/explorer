package com.muyoucai.util;

import com.google.common.base.Strings;
import com.muyoucai.ex.CustomException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author lzy
 */
public class StreamKit {

    public static void write(String content, String filepath) {
        if (!Strings.isNullOrEmpty(content)) {
            try (
                    FileWriter fw = new FileWriter(filepath);
                    BufferedWriter bw = new BufferedWriter(fw);
            ) {
                bw.write(content);
            } catch (IOException e) {
                throw new CustomException(e);
            }
        }
    }

}
