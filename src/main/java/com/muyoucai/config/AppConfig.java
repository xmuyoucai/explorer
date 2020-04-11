package com.muyoucai.config;

import com.muyoucai.framework.annotation.LzyBean;
import com.muyoucai.framework.annotation.LzyConfiguration;
import com.muyoucai.framework.annotation.LzyAutowired;
import com.muyoucai.framework.LzyEnvironment;
import com.muyoucai.storage.LzyDataSource;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


/**
 * @Description
 * @Author lzy
 * @Date 2020/3/31 22:45
 * @Version 1.0
 **/
@LzyConfiguration
public class AppConfig {

    @LzyAutowired
    private LzyEnvironment env;

    @LzyBean
    public CredentialsProvider credentialsProvider() {
        return new UsernamePasswordCredentialsProvider(env.getString("git.user"), env.getString("git.pass"));
    }

    @LzyBean
    public LzyDataSource lzyDataSource(){
        return LzyDataSource.builder().url("jdbc:h2:file:~/.h2/epl;FILE_LOCK=SOCKET").driver("org.h2.Driver").user("lzy").pass("123456").build();
    }

}
