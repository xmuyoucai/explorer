package com.muyoucai.storage.dao;


import com.muyoucai.storage.entity.Dictionary;

import java.util.List;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/11 20:29
 * @Version 1.0
 **/
public interface IDictionaryAccess {

    void addDictionary(String key, String value);

    void removeDictionary(String key);

    Dictionary getDictionary(String key);

    List<Dictionary> listDictionary();

}
