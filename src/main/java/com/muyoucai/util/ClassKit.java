package com.muyoucai.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Set;

@Slf4j
public class ClassKit {

    public static Set<String> scan(String baseDir, String scanDir, Set<String> set){
        String fullPath = baseDir + scanDir;
        File file = new File(fullPath);
        if(file.isDirectory()){
            String[] paths = file.list();
            if(paths != null && paths.length >= 0){
                for (int i = 0; i < paths.length; i++) {
                    scan(baseDir, scanDir + "/" + paths[i], set);
                }
            }
        }
        if(file.isFile()){
            if(fullPath.endsWith(".class")){
                set.add(fullPath.replace(baseDir, "").replace(".class", "").replace("/", "."));
            }
        }
        return set;
    }

    public static Set<String> scan(String packageName){
        String scanDir = packageName.replace(".", "/");
        String baseDir = ClassKit.class.getResource("/").toString().replace("file:/", "");
        return scan(baseDir, scanDir, Sets.newHashSet());
    }

    public static void main(String[] args) {
        Set<String> set = scan("com.muyoucai.util");
        for (String s : set) {
            System.out.println(s);
        }
    }

}
