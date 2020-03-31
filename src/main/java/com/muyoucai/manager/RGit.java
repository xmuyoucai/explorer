package com.muyoucai.manager;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.muyoucai.annotation.MBean;
import com.muyoucai.common.Cfg;
import com.muyoucai.ex.CustomException;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.StreamKit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

/**
 * @author lzy
 */
@Slf4j
@MBean
public class RGit {

    @Getter
    private String uri, localDir;

    private CredentialsProvider credentialsProvider;

    public RGit() {
        localDir = Cfg.PROPERTIES.getProperty("git.localDir");
        uri = Cfg.PROPERTIES.getProperty("git.uri");
        String user = Cfg.PROPERTIES.getProperty("git.user");
        String pass = Cfg.PROPERTIES.getProperty("git.pass");
        credentialsProvider = new UsernamePasswordCredentialsProvider(user, pass);
    }

    public Repository initRepo() {
        try (Git git = Git.init().setDirectory(FileKit.openOrCreateDir(localDir)).call()) {
            return git.getRepository();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public Repository cloneRepo() {
        try (Git result = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(FileKit.openOrCreateDir(localDir))
                .setCredentialsProvider(credentialsProvider)
                .call()) {
            return result.getRepository();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public Repository openRepo(){
        try (Git git = Git.open(new File(localDir))) {
            return git.getRepository();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public DirCache add(){
        try (Git git = Git.open(new File(localDir))) {
            return git.add().addFilepattern(".").call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public RevCommit commit(){
        try (Git git = Git.open(new File(localDir))) {
            return git.commit().setMessage("By explorer").call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public Iterable<PushResult> push(){
        try (Git git = Git.open(new File(localDir))) {
            return git.push().setCredentialsProvider(credentialsProvider).call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void pull(){
        try (Git git = Git.open(new File(localDir))) {
            git.push().call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public boolean exists(String path){
        return FileKit.exists(localDir + "/" + path);
    }

    public void createDirIfNotExists(String path){
        FileKit.openOrCreateDir(localDir + "/" + path);
    }

    public void createFileIfNotExists(String path){
        FileKit.openOrCreateFile(localDir + "/" + path);
    }

    public void upload(String filepath){
        DirCache dirCache = add();
        for (int i = 0; i < dirCache.getEntryCount(); i++) {
            log.info("added : {} - {}", dirCache.getEntry(i).getObjectId(), dirCache.getEntry(i).getPathString());
        }
        RevCommit revCommit = commit();
        log.info("commit : {}", revCommit.toString());
        Iterable<PushResult> pushResults = push();
        for (PushResult pr : pushResults) {
            log.info("pushed : ", pr.getURI().toString());
        }
    }

    public void write(String msg){
        StreamKit.write(msg, localDir + "/abc.txt");
    }

    public static void main(String[] args) {
        RGit rGit = new RGit();
        System.out.println("write ...");
        rGit.write("2323232");
        System.out.println("add ...");
        rGit.add();
        System.out.println("commit ...");
        rGit.commit();
        System.out.println("push ...");
        rGit.push();
    }

}