package com.example.demo2.controller;

import java.util.HashMap;
import java.util.Map;

class LRUCache {

    private Map<Integer, Node> map = new HashMap<>();
    private DoubleList cache = new DoubleList();
    private int cap = 0;

    public LRUCache(int cap) {
        this.cap = cap;
    }

    class Node {

        private Integer key, value;
        private Node prev, next;
        private String name;

        public Node(String name) {
            this.name = name;
        }

        public Node(Integer key, Integer value) {
            this.key = key;
            this.value = value;
        }
    }

    class DoubleList {

        private Node head = new Node("头");
        private Node tail = new Node("尾");
        private int size = 0;

        DoubleList() {
            head.next = tail;
            tail.prev = head;
        }

        /**
         * 将节点添加到末尾 note: 双链表的插入节点: 指针变动顺序不是唯一的, 但也不是任意的.
         *
         * @param node
         */
        public Node addLast(Node node) {
            node.next = tail;
            node.prev = tail.prev;
            tail.prev.next = node;
            tail.prev = node;
            size++;
            return node;
        }

        /**
         * 删除指定节点
         *
         * @param node 节点一定存在
         */
        public void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        /**
         * 删除第一个节点
         *
         * @return key
         */
        public Integer removeFirst() {
            Node node = head.next;
            remove(node);
            return node.key;
        }

    }

    public Integer get(Integer key) {
        if (!map.containsKey(key)) {
            return -1;
        }
        // TODO 这里私有的能访问到吗: A: 可以直接访问
        makeRecently(key);
        return map.get(key).value;
    }

    public void put(Integer key, Integer value) {
        // key 已经存在
        if (map.get(key) != null) {
            deleteKey(key);
            // 添加到最近使用
            addRecently(key, value);
            return;
        }

        // key 不存在
        // 是否容器是否满了
        if (isFull()) {
            removeLeastRecently();
        }
        // 满了, 删除
        // 添加到最近使用
        addRecently(key, value);
    }

    public boolean isFull() {
        return cap == map.size();
    }

    /**
     * 删除最旧未使用的.
     */
    public void removeLeastRecently() {
        // 删除第一个节点, 并返回key
        Integer key = cache.removeFirst();
        // 哈希表中删除
        map.remove(key);
    }

    public void deleteKey(Integer key) {
        Node node = map.get(key);
        if (node == null) {
            return;
        }
        cache.remove(node);
        map.remove(key);
    }

    /**
     * KV添加到最近使用
     *
     * @param key
     * @param value
     */
    public void addRecently(Integer key, Integer value) {
        // 双链表添加到末尾
        Node node = new Node(key, value);
        cache.addLast(node);
        // 哈希表中存放KV
        map.put(key, node);
    }


    /**
     * 将对应键值提升为最近使用
     *
     * @param key
     */
    public void makeRecently(Integer key) {
        Node node = map.get(key);
        cache.remove(node);
        cache.addLast(node);
    }
}
