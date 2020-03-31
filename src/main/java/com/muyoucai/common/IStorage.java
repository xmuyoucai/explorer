package com.muyoucai.common;

/**
 * @author lzy
 */
public interface IStorage<T> {

    boolean exists();

    void save(T data);

    T get();

}
