package com.muyoucai.config;

import com.muyoucai.framework.annotation.LzyBean;
import com.muyoucai.framework.annotation.LzyConfiguration;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.LzyEnvironment;
import com.muyoucai.storage.DataSourceFactory;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.hibernate.SessionFactory;


/**
 * @Description
 * @Author lzy
 * @Date 2020/3/31 22:45
 * @Version 1.0
 **/
@LzyConfiguration
public class AppConfig {

    @Autowired
    private LzyEnvironment env;

    @LzyBean
    public CredentialsProvider credentialsProvider() {
        return new UsernamePasswordCredentialsProvider(env.getString("git.user"), env.getString("git.pass"));
    }

}
