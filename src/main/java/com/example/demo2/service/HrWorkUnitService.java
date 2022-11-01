package com.example.demo2.service;

import com.example.demo2.constants.CacheNameConsts;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * @author wlei3
 * @since 2022/10/27 17:42
 */
@Slf4j
@Service
public class HrWorkUnitService {

    /**
     * 测试 @Cacheable key on multiple method arguments
     * hr:core:findParentToRootDid::SimpleKey [60000004,[expired, effective]]
     * @param cid
     * @param workUnitStatusList
     * @return
     */
    @SneakyThrows
    @Cacheable(value = CacheNameConsts.WORK_UNIT_V2)
    public Map<String, Object> findParentToRootDid(Long cid, Collection<String> workUnitStatusList) {
        Thread.sleep(3000);
        return ImmutableMap.of("1", "1");
    }
}
