package com.muyoucai.repository;

import com.muyoucai.entity.po.SystemConfig;
import com.muyoucai.framework.annotation.Component;
import com.muyoucai.framework.storage.FileStorage;
import com.muyoucai.framework.storage.GitStorage;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 16:06
 * @Version 1.0
 **/
@Component
public class SystemConfigRepository extends FileStorage<SystemConfig> {
    @Override
    public Class<SystemConfig> getEntityClass() {
        return SystemConfig.class;
    }

}
