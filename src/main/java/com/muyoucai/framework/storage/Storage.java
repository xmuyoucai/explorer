package com.muyoucai.framework.storage;

/**
 * @author lzy
 */
public interface Storage<T> {

    /**
     * 初始化存储
     */
    void init();

    /**
     * 获取数据
     *
     * @return
     */
    T get();

    /**
     * 保存数据
     *
     * @param data
     */
    void save(T data);

}
