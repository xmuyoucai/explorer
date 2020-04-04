package com.muyoucai.framework.storage;

import java.util.List;

/**
 * @author lzy
 */
public interface Storage<T> {

    /**
     * 获取列表
     *
     * @return
     */
    List<T> list();

    /**
     * 保存数据
     *
     * @param list
     */
    void save(List<T> list);

}
