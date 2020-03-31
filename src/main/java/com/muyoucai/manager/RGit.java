package com.muyoucai.manager;

import com.muyoucai.common.Cfg;
import com.muyoucai.ex.CustomException;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.StreamKit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

/**
 * @author lzy
 */
public class RGit {

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
        try (Git git = Git.init().setDirectory(FileKit.createDir(localDir)).call()) {
            return git.getRepository();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public Repository cloneRepo() {
        try (Git result = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(FileKit.createDir(localDir))
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

    public void add(String pattern){
        try (Git git = Git.open(new File(localDir))) {
            git.add().addFilepattern(pattern).call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void commit(String message){
        try (Git git = Git.open(new File(localDir))) {
            git.commit().setMessage(message).call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void push(){
        try (Git git = Git.open(new File(localDir))) {
            git.push().setCredentialsProvider(credentialsProvider).call();
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

    public void write(String msg){
        StreamKit.write(msg, localDir + "/abc.txt");
    }

    public static void main(String[] args) {
        // System.out.println(JSON.toJSONString(openRepo(localDir).getConfig()));
        RGit rGit = new RGit();
        // rGit.cloneRepo();
        System.out.println("write ...");
        rGit.write("2323232");
        System.out.println("add ...");
        rGit.add("abc.txt");
        System.out.println("commit ...");
        rGit.commit("1111");
        System.out.println("push ...");
        rGit.push();
    }

}
