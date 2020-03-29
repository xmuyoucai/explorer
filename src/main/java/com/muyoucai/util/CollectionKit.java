package com.muyoucai.util;

import java.util.Collection;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 16:14
 * @Version 1.0
 **/
public class CollectionKit {

    public static <E> boolean isEmpty(Collection<E> c) {
        return c == null || c.size() == 0;
    }

}
