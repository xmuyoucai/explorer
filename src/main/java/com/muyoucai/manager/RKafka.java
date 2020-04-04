package com.muyoucai.manager;

import com.alibaba.fastjson.JSON;
import com.muyoucai.util.KafkaAdminUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;

import java.util.Properties;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 22:43
 * @Version 1.0
 **/
public class RKafka {

    public RKafka() {

        Properties props = new Properties();
        props.put("bootstrap.servers", "120.78.200.102:9092");
        System.out.println("KafkaAdminUtils.describeCluster(props) :");
        System.out.println(JSON.toJSONString(KafkaAdminUtils.describeCluster(props)));
        System.out.println("KafkaAdminUtils.listConsumerGroups(props) : ");
        System.out.println(JSON.toJSONString(KafkaAdminUtils.listConsumerGroups(props)));
        System.out.println("KafkaAdminUtils.listPartitionReassignments(props) :");
        System.out.println(JSON.toJSONString(KafkaAdminUtils.listPartitionReassignments(props)));
        System.out.println("KafkaAdminUtils.metrics(props) : ");
        System.out.println(JSON.toJSONString(KafkaAdminUtils.metrics(props)));
    }

    public static void main(String[] args) {
        new RKafka();
    }

}
