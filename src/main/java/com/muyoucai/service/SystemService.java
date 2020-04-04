package com.muyoucai.service;

import com.alibaba.fastjson.JSON;
import com.muyoucai.entity.po.SystemConfig;
import com.muyoucai.framework.Environment;
import com.muyoucai.framework.Settings;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.annotation.Component;
import com.muyoucai.repository.SystemConfigRepository;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.GitUtils;
import com.muyoucai.util.StreamKit;
import org.eclipse.jgit.transport.CredentialsProvider;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 16:24
 * @Version 1.0
 **/
@Component
public class SystemService {

    @Autowired
    private Environment env;

    @Autowired
    private SystemConfigRepository scRepository;

    @Autowired
    private CredentialsProvider credentialsProvider;

    public void initialize(){
        // 基础路径
        String baseD = env.getString("epl.base-dir");
        if(!FileKit.exists(baseD)){
            FileKit.safelyCreateDir(baseD);
        }
        // 系统状态文件
        String statusF = String.format("%s/%s", baseD, Settings.STATUS_FILENAME);
        if(!FileKit.exists(statusF)){
            FileKit.safelyCreateFile(statusF);
            StreamKit.write(JSON.toJSONString(new SystemConfig()), statusF);
        }
        // 读取状态文件
        SystemConfig sc = scRepository.get();
        if(!sc.getStorageInitialized().booleanValue()){
            String absolutelyGitDir = String.format("%s/%s", baseD, env.get("git.dir"));
            GitUtils.create(absolutelyGitDir, env.get("git.uri"), credentialsProvider);
            sc.setStorageInitialized(true);
            scRepository.save(sc);
        }
    }

}
