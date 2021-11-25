package com.example.demo2.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Solution {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // 初始化入度表和邻接表
        int[] indeg = new int[numCourses];
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adjacency.add(new ArrayList<>());
        }

        // 计算入度表和邻接关系
        for (int[] node : prerequisites) {
            // 以 node[0] 节点为弧尾
            adjacency.get(node[1]).add(node[0]);
            indeg[node[0]]++;
        }

        // 初始化队列, 将入度为0的节点放入到队列中
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indeg[i] == 0) {
                queue.offer(i);
            }
        }

        // 当队列不为空的时候
        int visited = 0;
        while (!queue.isEmpty()) {
            visited++;
            // 出队
            Integer indeg0Node = queue.poll();
            // 将所有邻接的节点的入度 - 1
            for (Integer side : adjacency.get(indeg0Node)) {
                if (--indeg[side] == 0) {
                    queue.offer(side);
                }
            }
        }

        return visited == numCourses;
    }

    public static void main(String[] args) {
        int[][] prerequisites = {{0, 1}, {1, 2}, {2, 3}, {3, 1}};
        Solution solution = new Solution();
        boolean b = solution.canFinish(prerequisites.length + 1, prerequisites);
        System.out.println(b);
    }
}
