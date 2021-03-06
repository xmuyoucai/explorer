package com.muyoucai.repository;

import com.muyoucai.entity.po.SystemConfig2;
import com.muyoucai.framework.annotation.LzyComponent;
import com.muyoucai.framework.storage.FileStorage;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 16:06
 * @Version 1.0
 **/
@LzyComponent
public class SystemConfigRepository extends FileStorage<SystemConfig2> {
    @Override
    public Class<SystemConfig2> getEntityClass() {
        return SystemConfig2.class;
    }

}
