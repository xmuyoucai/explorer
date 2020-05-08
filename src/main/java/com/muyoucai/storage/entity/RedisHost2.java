package com.muyoucai.storage.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "redis_host")
public class RedisHost2 {

    @Id
    @TableId(value = "id", type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "host")
    private String host;
    @Column(name = "port")
    private Integer port;
    @Column(name = "pass")
    private String pass;

}
