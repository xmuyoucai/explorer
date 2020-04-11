package com.muyoucai.framework.storage;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.muyoucai.framework.LzyEnvironment;
import com.muyoucai.framework.annotation.LzyAutowired;
import com.muyoucai.framework.annotation.GitFile;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.StreamKit;
import com.muyoucai.util.ex.MissingGitFileAnnotationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.CredentialsProvider;
import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 10:52
 * @Version 1.0
 **/
@Slf4j
public abstract class GitStorage<T> implements Storage<T> {

    public abstract Class<T> getEntityClass();

    @LzyAutowired
    protected CredentialsProvider credentialsProvider;

    @LzyAutowired
    protected LzyEnvironment env;

    public List<T> list(){
        // GitUtils.pull(gitDir, credentialsProvider);
        String filepath = getFullPath();
        if(!FileKit.exists(filepath)) {
            FileKit.safelyCreateFile(filepath);
            StreamKit.write(JSON.toJSONString(Lists.newArrayList()), filepath);
        }
        String json = StreamKit.read(getFullPath());
        log.debug("load data : {}", json);
        return JSON.parseArray(json, getEntityClass());
    }

    @Override
    public void save(List<T> list) {
        String json = JSON.toJSONString(list);
        log.debug("persist data : {}", json);
        String filepath = getFullPath();
        if(!FileKit.exists(filepath)) {
            FileKit.safelyCreateFile(filepath);
        }
        StreamKit.write(json, filepath);
        // GitUtils.push(getGitDir(), credentialsProvider);
    }

    private String getBaseDir(){
        return env.get("epl.base-dir");
    }

    private String getGitDir(){
        return String.format("%s/%s", getBaseDir(), env.get("git.dir"));
    }

    private String getFilepath() {
        log.info("env : {}", JSON.toJSONString(env));
        GitFile gitFile = getEntityClass().getAnnotation(GitFile.class);
        if(gitFile == null){
            throw new MissingGitFileAnnotationException(getEntityClass().getCanonicalName());
        }
        String filepath;
        if(!Strings.isNullOrEmpty(gitFile.value())){
            filepath = gitFile.value();
        } else {
            filepath =  getEntityClass().getCanonicalName();
        }
        return filepath;
    }

    private String getFullPath(){
        return String.format("%s/%s", getGitDir(), getFilepath());
    }

}
