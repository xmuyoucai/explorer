package com.muyoucai.storage;

/**
 * @author lzy
 */
public interface IStorage<T> {

    void save(T data);

    T get();

}
