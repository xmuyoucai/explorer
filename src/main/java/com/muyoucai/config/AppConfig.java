package com.muyoucai.config;

import com.muyoucai.framework.annotation.Bean;
import com.muyoucai.framework.annotation.Configuration;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.Environment;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/31 22:45
 * @Version 1.0
 **/
@Configuration
public class AppConfig {

    @Autowired
    private Environment env;

    @Bean
    public CredentialsProvider credentialsProvider(){
        return new UsernamePasswordCredentialsProvider(env.getString("git.user"), env.getString("git.pass"));
    }

}
