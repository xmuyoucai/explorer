package com.muyoucai.storage;

import com.muyoucai.storage.dao.IDictionaryAccess;
import com.muyoucai.storage.dao.IRedisHostAccess;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/11 20:35
 * @Version 1.0
 **/
public interface ILzyStorage {

    IRedisHostAccess getRedisHostStorage();

    IDictionaryAccess getDictionaryStorage();

}
