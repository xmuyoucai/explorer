package com.muyoucai.storage.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "redis_host")
public class RedisHost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "host")
    private String host;
    @Column(name = "port")
    private Integer port;
    @Column(name = "pass")
    private String pass;

}
