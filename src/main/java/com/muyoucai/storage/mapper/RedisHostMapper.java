package com.muyoucai.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muyoucai.storage.entity.RedisHost2;
import org.apache.ibatis.annotations.Select;

public interface RedisHostMapper extends BaseMapper<RedisHost2> {

    @Select("select 1")
    String select1();

}
