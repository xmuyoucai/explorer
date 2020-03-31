package com.muyoucai.util;

import com.alibaba.fastjson.JSON;
import com.muyoucai.ex.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

/**
 * @author lzy
 */
public class GitKit {

    public static Repository initRepo(String localDir) {
        try (Git git = Git.init().setDirectory(FileKit.createDir(localDir)).call()) {
            return git.getRepository();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static Repository createRepo(String localDir, String repo) {
        try (Git result = Git.cloneRepository()
                .setURI(repo)
                .setDirectory(FileKit.createDir(localDir))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("xmuyoucai@qq.com", ".myc724815"))
                .call()) {
            return result.getRepository();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static Repository openRepo(String localDir){
        try (Git git = Git.open(new File(localDir))) {
            return git.getRepository();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static void add(String localDir){
        try (Git git = Git.open(new File(localDir))) {
            git.add().addFilepattern("*").call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static void commit(String message, String localDir){
        try (Git git = Git.open(new File(localDir))) {
            git.commit().setMessage(message).call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static void push(String localDir){
        try (Git git = Git.open(new File(localDir))) {
            git.push().call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static void pull(String localDir){
        try (Git git = Git.open(new File(localDir))) {
            git.push().call();
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static void main(String[] args) {
        String localDir = "D:/temp/jgit01";
        // StreamKit.write("134", localDir + "/abc.txt");
        // System.out.println(JSON.toJSONString(openRepo(localDir).getConfig()));
        push(localDir);
    }

}
