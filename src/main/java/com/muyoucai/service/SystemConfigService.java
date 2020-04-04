package com.muyoucai.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.muyoucai.entity.po.SystemConfig;
import com.muyoucai.framework.Environment;
import com.muyoucai.framework.Settings;
import com.muyoucai.framework.annotation.Autowired;
import com.muyoucai.framework.annotation.Component;
import com.muyoucai.repository.SystemConfigRepository;
import com.muyoucai.util.BeanKit;
import com.muyoucai.util.FileKit;
import com.muyoucai.util.GitUtils;
import com.muyoucai.util.StreamKit;
import org.eclipse.jgit.transport.CredentialsProvider;

import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 16:24
 * @Version 1.0
 **/
@Component
public class SystemConfigService {

    @Autowired
    private Environment env;

    @Autowired
    private SystemConfigRepository scRepository;

    @Autowired
    private CredentialsProvider credentialsProvider;

    public List<SystemConfig> list(){
        return scRepository.list();
    }

    public SystemConfig get(String key){
        for (SystemConfig sc : list()) {
            if(sc.getKey().equals(key)){
                return sc;
            }
        }
        return null;
    }

    public SystemConfig get(String key, List<SystemConfig> list){
        for (SystemConfig sc : list) {
            if(sc.getKey().equals(key)){
                return sc;
            }
        }
        return null;
    }

    public boolean exists(String key){
        return get(key) != null;
    }

    public void saveOrUpdate(SystemConfig sc){
        System.out.println(JSON.toJSONString(sc));
        List<SystemConfig> list = list();
        if(!exists(sc.getKey())){
            list.add(sc);
        } else {
            SystemConfig entity = get(sc.getKey(), list);
            BeanKit.copy(sc, entity);
        }
        System.out.println(JSON.toJSONString(list));
        scRepository.save(list);
    }

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
            StreamKit.write(JSON.toJSONString(Lists.newArrayList(new SystemConfig(ConfigKey.STORAGE_INITIALIZED.name(), String.valueOf(false)))), statusF);
        }
        // 读取状态文件
        SystemConfig sc = get(ConfigKey.STORAGE_INITIALIZED.name());
        if(!Boolean.valueOf(sc.getValue())){
            String absolutelyGitDir = String.format("%s/%s", baseD, env.get("git.dir"));
            GitUtils.create(absolutelyGitDir, env.get("git.uri"), credentialsProvider);
            sc.setValue(String.valueOf(true));
            saveOrUpdate(sc);
        }
    }

    public static void main(String[] args) {
        System.out.println(String.valueOf(true));
    }

    public enum ConfigKey {
        STORAGE_INITIALIZED
    }

}