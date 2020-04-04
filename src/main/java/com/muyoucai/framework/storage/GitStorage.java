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
    protected String baseDir, statusFile, gitDir, gitUri;
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

        this.gitDir = env.getString("git.dir");
        this.gitUri = env.getString("git.uri");

        this.baseDir = env.getString("epl.baseDir");
        this.statusFile = "status.json";

        if(!FileKit.exists(baseDir)){
            FileKit.safelyCreateDir(baseDir);
        }
        String statusFilepath = String.format("%s%s", baseDir, statusFile);
        if(FileKit.exists(statusFilepath)){
            FileKit.safelyCreateFile(statusFilepath);
        }
        FileKit.safelyCreateFile(statusFile);
        FileKit.safelyCreateDir(gitDir);
        GitUtils.create(gitDir, gitUri, credentialsProvider);
    }

    @Override
    public T get() {
        GitUtils.pull(gitDir, credentialsProvider);
        String json = StreamKit.read(absolutelyFilepath());
        log.debug("load data : {}", json);
        return JSON.toJavaObject(JSON.parseObject(json), getEntityClass());
    }

    @Override
    public void save(T data) {
        String json = JSON.toJSONString(data);
        log.debug("persist data : {}", json);
        StreamKit.write(json, absolutelyFilepath());
        GitUtils.push(absolutelyGitDir(), credentialsProvider);
    }

    private String absolutelyFilepath(){
        return gitDir + filepath();
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

    private String absolutelyGitDir(){
        if(Strings.isNullOrEmpty(gitDir)){
            gitDir = baseDir + "/db";
        }
        if(gitDir.endsWith("/")){
            gitDir = gitDir.substring(0, gitDir.lastIndexOf("/"));
        }
        if(!FileKit.exists(gitDir)){
            FileKit.safelyCreateDir(gitDir);
        }
        return gitDir;
    }

}
