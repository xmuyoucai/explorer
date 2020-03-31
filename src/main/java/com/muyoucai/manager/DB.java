package com.muyoucai.manager;

import com.google.common.base.Strings;
import com.muyoucai.annotation.MBean;
import com.muyoucai.annotation.MValue;
import com.muyoucai.core.Settings;
import com.muyoucai.ex.CustomException;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.StreamKit;
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
public class DB {

    @MValue("git.localDir")
    private String localDir;

    @MValue("git.uri")
    private String uri;

    @MValue("git.user")
    private String user;

    @MValue("git.pass")
    private String pass;

    private CredentialsProvider credentialsProvider;

    public DB() {
        String user = Settings.PROPERTIES.getProperty("git.user");
        String pass = Settings.PROPERTIES.getProperty("git.pass");
        credentialsProvider = new UsernamePasswordCredentialsProvider(user, pass);
    }

    public boolean exists(){
        if(Strings.isNullOrEmpty(localDir)){
            throw new CustomException("[git.localDir] is not configured");
        }
        return FileKit.exists(localDir);
    }

    public void create(){
        if(Strings.isNullOrEmpty(uri)){
            throw new CustomException("[git.uri] is not configured");
        }
        if(Strings.isNullOrEmpty(user)){
            throw new CustomException("[git.user] is not configured");
        }
        if(Strings.isNullOrEmpty(pass)){
            throw new CustomException("[git.pass] is not configured");
        }
        FileKit.openOrCreateDir(localDir);
        log.info("clone ..");
        cloneRepo();
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
        DB gitManager = new DB();
        System.out.println("write ...");
        gitManager.write("2323232");
        System.out.println("add ...");
        gitManager.add();
        System.out.println("commit ...");
        gitManager.commit();
        System.out.println("push ...");
        gitManager.push();
    }

}
