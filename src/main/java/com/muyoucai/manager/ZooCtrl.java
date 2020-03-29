package com.muyoucai.manager;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.muyoucai.util.CollectionKit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 15:51
 * @Version 1.0
 **/
@Slf4j
public class ZooCtrl {

    private static ZooKeeper zoo;

    public static void connect(String addresses) throws IOException {
        if(zoo == null){
            zoo = new ZooKeeper(addresses, 5000, new ZooWatcher());
        }
    }

    public static void fetchData(Node node) throws KeeperException, InterruptedException, IOException {
        connect("120.78.200.102:2181");
        try {
            log.info("path : {}", node.getPath());
            node.setData(String.valueOf(zoo.getData(node.getPath(), false, new Stat())));

            List<String> children = zoo.getChildren(node.getPath(), false);
            if(!CollectionKit.isEmpty(children)){
                log.info("children : {}", JSON.toJSONString(children));
                node.getChildren().addAll(children.stream().map(path -> createNode(node.getPath(), path)).collect(Collectors.toList()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Node child : node.getChildren()) {
            fetchData(child);
        }
    }

    private static Node createNode(String parent, String node) {
        String path;
        if("/".equals(parent)){
            path = String.format("/%s", node);
        } else {
            path = String.format("%s/%s", parent, node);
        }
        return new Node("/" + node, path);
    }

    @Getter
    @Setter
    public static class Node {
        private String path;
        private String node;
        private String data;
        @JsonIgnore
        private List<Node> children;

        public Node(String node, String path) {
            this.node = node;
            this.path = path;
            this.children = Lists.newArrayList();
        }
    }

    @Slf4j
    public static class ZooWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            log.info("zoo watch : {}", JSON.toJSONString(event));
        }
    }

}
