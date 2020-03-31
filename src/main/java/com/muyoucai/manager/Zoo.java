package com.muyoucai.manager;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.muyoucai.util.CollectionKit;
import com.muyoucai.view.FxUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author lzy
 * @Date 2020/3/29 15:51
 * @Version 1.0
 **/
@Slf4j
public class Zoo {

    @Getter
    private Node data;

    public Zoo(String address) {
        try (ZooKeeper zoo = new ZooKeeper(address, 5000, new ZooWatcher())) {
            fetchData((data = new Node("/", "/", 1)), zoo);
        } catch (Exception e) {
            FxUtils.error(String.format("获取 [%s] 数据失败", address));
        }
    }

    public void fetchData(Node node, ZooKeeper zoo) {
        log.debug("path : {}", node.getPath());
        node.setData(getData(node.getPath(), zoo));
        node.setChildren(getChildren(node, zoo));
        for (Node child : node.getChildren()) {
            fetchData(child, zoo);
        }
    }

    private List<Node> getChildren(Node parent, ZooKeeper zoo) {
        List<Node> nodes = Lists.newArrayList();
        try {
            List<String> children = zoo.getChildren(parent.getPath(), false);
            if(!CollectionKit.isEmpty(children)){
                nodes.addAll(children.stream().map(child -> createNode(child, parent)).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            FxUtils.error(String.format("获取 [%s] 的子节点失败：%s", parent.getPath(),  e.getLocalizedMessage()));
        }
        return nodes;
    }

    private String getData(String path, ZooKeeper zoo){
        try {
            byte[] bytes;
            if((bytes = zoo.getData(path, false, null)) != null){
                return new String(bytes);
            }
        } catch (Exception e) {
            FxUtils.error(String.format("获取 [%s] 节点数据失败: %s", path, e.getLocalizedMessage()));
        }
        return null;
    }

    private Node createNode(String child, Node parent) {
        String path, name = child;
        if("/".equals(parent.getPath())){
            path = String.format("/%s", child);
        } else {
            path = String.format("%s/%s", parent.getPath(), child);
            name = "/" + name;
        }
        return new Node(name, path, parent.getLevel() + 1);
    }

    @Getter
    @Setter
    public static class Node {
        private String id; // ID
        private String name; // 节点名称
        private String data; // 节点数据
        private int level; // 层级
        private String path; // 全路径
        @JsonIgnore
        private List<Node> children;

        public Node() {
            id = UUID.randomUUID().toString();
            this.level = 0;
            this.children = Lists.newArrayList();
        }

        public Node(String name) {
            id = UUID.randomUUID().toString();
            this.name = name;
            this.level = 1;
            this.children = Lists.newArrayList();
        }

        public Node(String name, String path, int level) {
            id = UUID.randomUUID().toString();
            this.name = name;
            this.path = path;
            this.level = level;
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
