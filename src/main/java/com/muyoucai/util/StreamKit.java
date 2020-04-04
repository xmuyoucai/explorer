package com.muyoucai.util;

import com.google.common.base.Strings;
import com.muyoucai.util.ex.CustomException;

import java.io.*;

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

    public static String read(String filepath) {
        try (
                FileReader fr = new FileReader(filepath);
                BufferedReader br = new BufferedReader(fr);
        ) {
            StringBuffer ret = new StringBuffer();
            String s;
            while ((s = br.readLine()) != null) {
                ret.append(s);
            }
            return ret.toString();
        } catch (IOException e) {
            throw new CustomException(e);
        }
    }

}
