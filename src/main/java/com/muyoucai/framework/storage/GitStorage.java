package com.muyoucai.framework.storage;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.muyoucai.framework.Environment;
import com.muyoucai.framework.ReflectKit;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.annotation.GitFile;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.GitUtils;
import com.muyoucai.util.StreamKit;
import com.muyoucai.util.ex.MissingGitFileAnnotationException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.CredentialsProvider;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 10:52
 * @Version 1.0
 **/
@Slf4j
public abstract class GitStorage<T> implements Storage<T> {
    // 认证凭证
    @Autowired
    protected CredentialsProvider credentialsProvider;

    @Autowired
    protected Environment env;

    public abstract Class<T> getEntityClass();

    @Override
    public T get() {
        String gitDir = getGitDir();
        // GitUtils.pull(gitDir, credentialsProvider);
        String filepath = getFullPath();
        if(!FileKit.exists(filepath)){
            save(ReflectKit.newInstance(getEntityClass()));
        }
        String json = StreamKit.read(filepath);
        log.debug("load data : {}", json);
        return JSON.toJavaObject(JSON.parseObject(json), getEntityClass());
    }

    @Override
    public void save(T data) {
        String json = JSON.toJSONString(data);
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
