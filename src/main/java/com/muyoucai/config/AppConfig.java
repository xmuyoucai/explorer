package com.muyoucai.config;

import com.muyoucai.framework.annotation.Bean;
import com.muyoucai.framework.annotation.Configuration;
import com.muyoucai.framework.annotation.Autowire;
import com.muyoucai.framework.Environment;
import lombok.AllArgsConstructor;
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

    @Autowire
    private Environment env;

    @Bean
    public CredentialsProvider credentialsProvider(){
        return new UsernamePasswordCredentialsProvider("1111", "222");
    }

    @Bean
    public MMM mmm(){
        return new MMM(credentialsProvider());
    }

    @AllArgsConstructor
    public static class MMM {
        private CredentialsProvider credentialsProvider;
    }

}
