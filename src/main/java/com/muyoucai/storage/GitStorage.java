package com.muyoucai.storage;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;

import java.io.File;

/**
 * @author lzy
 */
@Slf4j
public class GitStorage implements IStorage {

    @Override
    public void save(RedisServer redisServer) {

    }

}
