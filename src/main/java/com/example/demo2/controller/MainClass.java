package com.example.demo2.controller;

import java.util.ArrayList;
import java.util.List;

class Solution {

    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // 初始化邻接表和访问标记数组
        List<List<Integer>> adjacency = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adjacency.add(new ArrayList<>());
        }
        int[] flags = new int[numCourses];

        // 初始化边
        for (int[] cp : prerequisites) {
            adjacency.get(cp[1]).add(cp[0]);
        }

        // 深度遍历所有节点
        for (int i = 0; i < numCourses; i++) {
            if (!dfs(adjacency, flags, i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param adjacency
     * @param flags
     * @param i
     * @return true 不存在环, false 存在环
     */
    private boolean dfs(List<List<Integer>> adjacency, int[] flags, int i) {
        // 当前节点搜索中, 节点i被第二次访问, 存在环
        if (flags[i] == 1) {
            return false;
        }
        // 该节点已完成搜索, 不存在环, 无需继续遍历
        if (flags[i] == -1) {
            return true;
        }

        // 将其标记为: 搜索中
        flags[i] = 1;

        // 递归访问相邻的边
        for (Integer j : adjacency.get(i)) {
            // 存在环退出
            if (!dfs(adjacency, flags, j)) {
                return false;
            }
        }
        // 当前节点已完成搜索
        flags[i] = -1;
        return true;
    }


    public static void main(String[] args) {
        int numCourses = 4;
        int[][] prerequisites = {{0, 1}, {1, 2}, {2, 3}};
        Solution solution = new Solution();
        boolean b = solution.canFinish(numCourses, prerequisites);
        System.out.println(b);
    }
}
