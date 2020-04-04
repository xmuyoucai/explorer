package com.muyoucai.framework.storage;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.muyoucai.framework.Environment;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.annotation.GitFile;
import com.muyoucai.util.ex.FilepathNotConfiguredException;
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

    // 文件相对路径
    private String filepath;
    // 本地 repo 目录，远程 uri
    protected String dir, uri;
    // 认证凭证
    @Autowired
    protected CredentialsProvider credentialsProvider;

    @Autowired
    protected Environment env;

    public abstract Class<T> getEntityClass();

    @Override
    public void init() {
        log.info("env : {}", JSON.toJSONString(env));
        GitFile gitFile = getEntityClass().getAnnotation(GitFile.class);
        if(gitFile == null){
            throw new MissingGitFileAnnotationException(getEntityClass().getCanonicalName());
        }
        if(!Strings.isNullOrEmpty(gitFile.value())){
            this.filepath = gitFile.value();
        } else {
            this.filepath = getEntityClass().getCanonicalName();
        }

        this.dir = env.getString("git.dir");
        this.uri = env.getString("git.uri");

        if(!FileKit.exists(dir)){
            Preconditions.checkArgument(Strings.isNullOrEmpty(dir), "please configure 'git.repo' entry");
            Preconditions.checkArgument(Strings.isNullOrEmpty(uri), "please configure 'git.uri' entry");
            Preconditions.checkArgument(credentialsProvider != null, "Had you configured a org.eclipse.jgit.transport.CredentialsProvider bean ?");
            FileKit.openOrCreateDir(dir);
            GitUtils.create(dir, uri, credentialsProvider);
        }
    }

    @Override
    public T get() {
        GitUtils.pull(dir, credentialsProvider);
        String json = StreamKit.read(fullPath());
        log.debug("load data : {}", json);
        return JSON.toJavaObject(JSON.parseObject(json), getEntityClass());
    }

    @Override
    public void save(T data) {
        String json = JSON.toJSONString(data);
        log.debug("persist data : {}", json);
        StreamKit.write(json, fullPath());
        GitUtils.push(dir(), credentialsProvider);
    }

    private String fullPath(){
        return dir + filepath();
    }

    private String filepath(){
        if(Strings.isNullOrEmpty(filepath)){
            throw new FilepathNotConfiguredException();
        }
        if(!filepath.startsWith("/")){
            filepath = "/" + filepath;
        }
        return filepath;
    }

    private String dir(){
        if(Strings.isNullOrEmpty(dir)){
            dir = "D:/epl/db";
        }
        if(dir.endsWith("/")){
            dir = dir.substring(0, dir.lastIndexOf("/"));
        }
        if(!FileKit.exists(dir)){
            FileKit.openOrCreateDir(dir);
        }
        return dir;
    }

}
