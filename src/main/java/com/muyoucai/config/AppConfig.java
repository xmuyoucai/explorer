package com.muyoucai.config;

import com.muyoucai.annotation.MBean;
import com.muyoucai.annotation.MConfig;
import com.muyoucai.annotation.MInjector;
import com.muyoucai.core.Environment;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/31 22:45
 * @Version 1.0
 **/
@MConfig
public class AppConfig {

    @MInjector
    private Environment env;

    @MBean
    public CredentialsProvider credentialsProvider(){
        return new UsernamePasswordCredentialsProvider(env.getString("git.user"), env.getString("git.pass"));
    }

}
