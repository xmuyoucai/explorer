package com.muyoucai.util;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;

import java.util.Map;
import java.util.Properties;

/**
 * @Description
 * @Author lzy
 * @Date 2020/4/4 22:47
 * @Version 1.0
 **/
public class KafkaAdminUtils {

    public static ListTopicsResult listTopics(Properties props){
        try (AdminClient ac = KafkaAdminClient.create(props)) {
            return ac.listTopics();
        }
    }

    public static ListConsumerGroupsResult listConsumerGroups(Properties props){
        try (AdminClient ac = KafkaAdminClient.create(props)) {
            return ac.listConsumerGroups();
        }
    }

    public static ListPartitionReassignmentsResult listPartitionReassignments(Properties props){
        try (AdminClient ac = KafkaAdminClient.create(props)) {
            return ac.listPartitionReassignments();
        }
    }

    public static DescribeClusterResult describeCluster(Properties props){
        try (AdminClient ac = KafkaAdminClient.create(props)) {
            return ac.describeCluster();
        }
    }

    public static Map<MetricName, ? extends Metric> metrics(Properties props){
        try (AdminClient ac = KafkaAdminClient.create(props)) {
            return ac.metrics();
        }
    }

}
