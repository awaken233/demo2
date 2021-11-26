package com.example.demo2.controller;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

class Solution {

    /**
     * mock 的依赖关系图
     */
    public static ImmutableMap<String, ImmutableSet<String>> dependentBeanMap = ImmutableMap.of(
        "Z", ImmutableSet.of("A", "B"),
        "A", ImmutableSet.of("A1", "A2", "A3"),
        "B", ImmutableSet.of("B1", "B2", "B3"),
        "C", ImmutableSet.of("C1", "C2", "C3")
    );

    public static void main(String[] args) {
        // 当前 bean 的名称
        String beanName = "Z";
        // 当前 bean 所有的子 bean
        Set<String> dependsOn = dependentBeanMap.get(beanName);

        // DFS
        for (String dep : dependsOn) {
            if (isDependent(beanName, dep)) {
                String txt = StrUtil.format("{}和{}存在循环依赖", beanName, dep);
                throw new IllegalStateException(txt);
            }
        }
    }

    /**
     * @param beanName 当前bean
     * @param dependentBeanName 依赖的bean
     * @return true 存在依赖
     */
    public static boolean isDependent(String beanName, String dependentBeanName) {
        return isDependent(beanName, dependentBeanName, null);
    }

    /**
     * @param beanName 当前bean
     * @param dependentBeanName 当前bean的子bean
     * @param alreadySeen 已经遍历过的bean集合
     * @return
     */
    public static boolean isDependent(String beanName, String dependentBeanName, Set<String> alreadySeen) {
        if (alreadySeen != null && alreadySeen.contains(beanName)) {
            return false;
        }

        Set<String> dependentBeans = dependentBeanMap.get(beanName);
        if (dependentBeans == null) {
            return false;
        }
        // 当前bean的所有孩子 是否包含依赖的bean
        if (dependentBeans.contains(dependentBeanName)) {
            return true;
        }

        // 继续深度遍历传递依赖
        for (String transitiveDependency : dependentBeans) {
            if (alreadySeen == null) {
                alreadySeen = new HashSet<>();
            }
            alreadySeen.add(beanName);
            if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) {
                return true;
            }
        }
        return false;
    }
}
