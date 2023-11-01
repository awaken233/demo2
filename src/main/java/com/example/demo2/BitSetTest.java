package com.example.demo2;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author evtok
 * @since 2023/5/26 23:18
 */
public class BitSetTest {

    public static void main(String[] args) {
//        List<Long> list = LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
//
//        Map<Long, List<Long>> map = new HashMap<>();
//        for (Long aLong : list) {
//            map.computeIfAbsent(aLong, k -> new ArrayList<>())
//                    .add(aLong);
//        }
//        System.out.println(map);

        System.out.println(IdUtil.simpleUUID());

        // 原始数据
        String originalData = "Hello, Hutool!";

        // 密钥，16位
        String secretKey = "1234567890123456";

        // 加密
        String encryptResult = SecureUtil.aes(secretKey.getBytes(StandardCharsets.UTF_8)).encryptBase64(originalData);
        System.out.println("加密后的数据：" + encryptResult);

        // 解密
        String decryptResult = SecureUtil.aes(secretKey.getBytes(StandardCharsets.UTF_8)).decryptStr(encryptResult);
        System.out.println("解密后的数据：" + decryptResult);
    }
}




