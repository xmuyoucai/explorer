package com.muyoucai.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muyoucai.storage.entity.RedisHost;
import org.apache.ibatis.annotations.Select;

public interface RedisHostMapper extends BaseMapper<RedisHost> {

    @Select("select 1")
    String select1();

}
