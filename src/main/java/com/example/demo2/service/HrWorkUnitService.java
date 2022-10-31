package com.example.demo2.service;

import com.example.demo2.constants.CacheNameConsts;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author wlei3
 * @since 2022/10/27 17:42
 */
@Slf4j
@Service
public class HrWorkUnitService {

    @SneakyThrows
    @Cacheable(value = CacheNameConsts.WORK_UNIT_V2, key = "#cid")
    public Map<String, Object> findParentToRootDid(Long cid, Boolean containOwner,
        List<String> dids, Collection<String> workUnitStatusList) {
        Thread.sleep(3000);
        return ImmutableMap.of("1", "1");
    }
}
