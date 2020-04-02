package com.muyoucai.config;

import com.muyoucai.framework.annotation.Bean;
import com.muyoucai.framework.annotation.Configuration;
import com.muyoucai.framework.annotation.Autowire;
import com.muyoucai.framework.Environment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Bean(name = "mmm2")
    public MMM mmm2(){
        return new MMM(credentialsProvider());
    }

    @Bean(name = "mmm3")
    public MMM mmm3(){
        return new MMM(credentialsProvider());
    }


    @NoArgsConstructor
    @AllArgsConstructor
    public static class MMM {
        @Getter
        @Setter
        private CredentialsProvider credentialsProvider;
    }

}
