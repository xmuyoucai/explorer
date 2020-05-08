package com.muyoucai.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.muyoucai.entity.po.SystemConfig2;
import com.muyoucai.framework.LzyEnvironment;
import com.muyoucai.framework.Settings;
import com.muyoucai.framework.annotation.LzyAutowired;
import com.muyoucai.framework.annotation.LzyComponent;
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
@LzyComponent
public class SystemConfigService {

    @LzyAutowired
    private LzyEnvironment env;

    @LzyAutowired
    private SystemConfigRepository scRepository;

    @LzyAutowired
    private CredentialsProvider credentialsProvider;

    public List<SystemConfig2> list(){
        return scRepository.list();
    }

    public SystemConfig2 get(String key){
        for (SystemConfig2 sc : list()) {
            if(sc.getKey().equals(key)){
                return sc;
            }
        }
        return null;
    }

    public SystemConfig2 get(String key, List<SystemConfig2> list){
        for (SystemConfig2 sc : list) {
            if(sc.getKey().equals(key)){
                return sc;
            }
        }
        return null;
    }

    public boolean exists(String key){
        return get(key) != null;
    }

    public void saveOrUpdate(SystemConfig2 sc){
        System.out.println(JSON.toJSONString(sc));
        List<SystemConfig2> list = list();
        if(!exists(sc.getKey())){
            list.add(sc);
        } else {
            SystemConfig2 entity = get(sc.getKey(), list);
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
            StreamKit.write(JSON.toJSONString(Lists.newArrayList(new SystemConfig2(ConfigKey.STORAGE_INITIALIZED.name(), String.valueOf(false)))), statusF);
        }
        // 读取状态文件
        SystemConfig2 sc = get(ConfigKey.STORAGE_INITIALIZED.name());
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
