package com.muyoucai.util;

import com.muyoucai.util.ex.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import java.io.File;

/**
 * @author lzy
 */
@Slf4j
public class GitUtils {

    public static Repository init(String repo) {
        try (Git git = Git.init().setDirectory(FileKit.openOrCreateDir(repo)).setBare(true).call()) {
            return git.getRepository();
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static Repository create(String repo, String uri, CredentialsProvider credentialsProvider) {
        try (Git result = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(FileKit.openOrCreateDir(repo))
                .setCredentialsProvider(credentialsProvider)
                .setBare(true)
                .call()) {
            return result.getRepository();
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static Repository open(String repo){
        try (Git git = Git.open(FileKit.openOrCreateDir(repo))) {
            return git.getRepository();
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static void add(String pattern, String repo){
        try (Git git = Git.open(new File(repo))) {
            git.add().addFilepattern(pattern).call();
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static void commit(String message, String repo){
        try (Git git = Git.open(new File(repo))) {
            git.commit().setMessage(message).call();
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static void push(String repo, CredentialsProvider credentialsProvider){
        try (Git git = Git.open(new File(repo))) {
            git.push().setCredentialsProvider(credentialsProvider).call();
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

    public static void pull(String repo, CredentialsProvider credentialsProvider){
        try (Git git = Git.open(new File(repo))) {
            log.debug("pull repo ...");
            git.pull().setCredentialsProvider(credentialsProvider).call();
        } catch (Exception e) {
            log.error("", e);
            throw new CustomException(e);
        }
    }

}
