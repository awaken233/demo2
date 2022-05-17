package com.example.demo2.config;

import cn.hutool.core.util.StrUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * 凸包算法
 */
class Solution {
    public int[][] outerTrees(int[][] points) {
        // 如果树的棵数小于 4 ，那么直接返回. 也可以放在 set 中去重之后返回
        if (points.length < 4) {
            return points;
        }
        Set<int[]> hull = new HashSet<>();

        // 找到最左边的点所在的index
        // 判断X轴的最小值
        int leftMost = 0;
        for (int i = 1; i < points.length; i++) {
            if (points[i][0] < points[leftMost][0]
                || (points[i][0] == points[leftMost][0] && points[i][1] < points[leftMost][1])
            ) {
                // 下一个点 < 记录的X轴最小值点 -> 下一个点就是最小的点
                // 需要判断相同X轴时, 取Y轴的小值的坐标点, 否则将会导致死循环.
                leftMost = i;
            }
        }

        // 最左侧的点
        int p = leftMost;
        do {
            // 下一个点
            int q = (p + 1) % points.length;

            // 获取下一个最右侧的点 p->next(q)
            for (int r = 0; r < points.length; r++) {
                // 如果 r 点在 pq 线下方，则使用 r 点
                //             最左侧点    遍历寻找的点  下一个点
                // 如果r点在pq的
                if (orientation(points[p], points[r], points[q]) < 0) {
                    q = r;
                }
            }

            for (int i = 0; i < points.length; i++) {
                // p、q、i 在同一条线上的情况，并且 i 在 p 和 q 的中间的时候
                // 也需要将这个点算进来
                if (i != p && i != q
                    && orientation(points[p], points[i], points[q]) == 0
                    && inBetween(points[p], points[i], points[q])) {
                    hull.add(points[i]);
                }
            }

            hull.add(points[q]);
            // 重置 p 为 q，接着下一轮的遍历
            p = q;

            // 终点: 下一个点 == 最左侧的点
        } while (p != leftMost);

        return hull.toArray(new int[hull.size()][]);
    }

    // 叉乘的公式定义如下:
    // 以下 pq 和 qr 都是向量
    // pq * qr > 0 表示 r 点在 pq 线上方
    // pq * qr < 0 表示 r 点在 pq 线下方
    // pq * qr = 0 表示 p、q、r 一条线
    //           |(q[0]-p[0])<x1> (q[1]-p[1])<y1>|
    // pq * qr = |                               | = (q[0]-p[0]) * (r[1]-q[1]) - (r[0]-q[0]) * (q[1]-p[1])
    //           |(r[0]-q[0])<x2> (r[1]-q[1])<y2>|
    private int orientation(int[] p, int[] r, int[] q) {
        return (q[0] - p[0]) * (r[1] - q[1]) - (r[0] - q[0]) * (q[1] - p[1]);
    }

    // 判断 r 点是不是在 p 点和 q 点之间，需要考虑以下两种情况：
    // 1. q 点在 p 点的左边或者右边
    // 2. q 点在 p 点的上边或者下边
    private boolean inBetween(int[] p, int[] r, int[] q) {
        // r的X轴在pq之间 || r的X轴在pq之间两侧
        boolean a = r[0] >= p[0] && r[0] <= q[0] || r[0] <= p[0] && r[0] >= q[0];
        // r的Y轴在pq之间 || r的Y轴在pq之间两侧`
        boolean b = r[1] >= p[1] && r[1] <= q[1] || r[1] <= p[1] && r[1] >= q[1];
        return a && b;
    }

    public static void main(String[] args) {
        int[][] points = {{0, 5}, {10, 0}, {10, 10}, {0, 10}, {0, 0}};
        Solution solution = new Solution();
        int[][] outs = solution.outerTrees(points);
        for (int[] out : outs) {
            System.out.println(StrUtil.format("[{},{}]", out[0], out[1]));
        }
    }
}
