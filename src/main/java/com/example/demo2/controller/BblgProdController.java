package com.example.demo2.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo2.feign.BblgProdApi;
import com.example.demo2.prop.BblgProperties;
import com.example.demo2.vo.BaseDataVo;
import com.example.demo2.vo.RsaSignDemo;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wlei3
 * @since 2022/10/20 20:13
 */
@Slf4j
@RestController
@Data
public class BblgProdController {

    private final BblgProdApi bblgApi;
    private final BblgProperties bblgProperties;

    /**
     * 查询企业入住结果
     * @return
     */
    @SneakyThrows
    @PostMapping("queryCompanyInfo")
    public Object queryCompanyInfo() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("businessId", "QY2021110810000521");

        BaseDataVo vo = RsaSignDemo.createSendMsgVo(bblgProperties.getAppId(), bblgProperties.getPublicKey(),
                bblgProperties.getPrivateKey(), JSON.toJSONString(dataMap));
        BaseDataVo resp = bblgApi.queryCompanyInfo(vo);

        BaseDataVo baseDataVo = RsaSignDemo.decryptAndVerify(bblgProperties.getPublicKey(), bblgProperties.getPrivateKey(),
                JSON.toJSONString(resp));
        return JSON.parse(baseDataVo.getData());
    }


    /**
     * 查询项目
     * @return
     */
    @SneakyThrows
    @PostMapping("myItem")
    public Object myItem() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("businessId", "QY2021110810000521");

        BaseDataVo vo = RsaSignDemo.createSendMsgVo(bblgProperties.getAppId(), bblgProperties.getPublicKey(),
                bblgProperties.getPrivateKey(), JSON.toJSONString(dataMap));
        BaseDataVo resp = bblgApi.myItem(vo);

        BaseDataVo baseDataVo = RsaSignDemo.decryptAndVerify(bblgProperties.getPublicKey(), bblgProperties.getPrivateKey(),
                JSON.toJSONString(resp));
        return JSON.parse(baseDataVo.getData());
    }


    /**
     * 查询任务
     * @return
     */
    @SneakyThrows
    @PostMapping("mytask")
    public Object mytask() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("itemId", "1229795560122425344");
        dataMap.put("businessId", "QY2021110810000521");

        log.info("dataMap:{}", JSON.toJSONString(dataMap));
        BaseDataVo vo = RsaSignDemo.createSendMsgVo(bblgProperties.getAppId(), bblgProperties.getPublicKey(),
                bblgProperties.getPrivateKey(), JSON.toJSONString(dataMap));
        BaseDataVo resp = bblgApi.mytask(vo);

        BaseDataVo baseDataVo = RsaSignDemo.decryptAndVerify(bblgProperties.getPublicKey(), bblgProperties.getPrivateKey(), JSON.toJSONString(resp));
        return JSON.parse(baseDataVo.getData());
    }


    @SneakyThrows
    @PostMapping("settleQueryBatch")
    public Object settleQueryBatch() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("outSeqNo", "1713511828225");
        dataMap.put("outBatchNo", "1713511828225");
        dataMap.put("businessId", "QY2021110810000521");

        log.info("dataMap:{}", JSON.toJSONString(dataMap));
        BaseDataVo vo = RsaSignDemo.createSendMsgVo(bblgProperties.getAppId(), bblgProperties.getPublicKey(),
                bblgProperties.getPrivateKey(), JSON.toJSONString(dataMap));
        BaseDataVo resp = bblgApi.settleQueryBatch(vo);
        log.info("resp:{}", JSON.toJSONString(resp));

        BaseDataVo baseDataVo = RsaSignDemo.decryptAndVerify(bblgProperties.getPublicKey(), bblgProperties.getPrivateKey(), JSON.toJSONString(resp));
        return JSON.parse(baseDataVo.getData());
    }

    /**
     * 批量结算
     *
     * @return
     */
    @SneakyThrows
    @PostMapping("settleBatch")
    public Object settleBatch() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("businessId", "QY2021110810000521");
        dataMap.put("taskId", "1229796339063398400");
        // 微信通道子帐号
        dataMap.put("acctNo", "30206465589904");
        // 结算批次号，要求企业内唯一
        dataMap.put("outBatchNo", System.currentTimeMillis()+"");
        dataMap.put("notityUrl", "http://www.baidu.com");
        dataMap.put("toDoNotityUrl", "");
        // 批次总金额(整数，单位:分)
        dataMap.put("totalSettleFee", 30);
        dataMap.put("total", 1);

        Map<String, Object> freMap = new HashMap<>();
        // 同一批次交易里面，序列号不允许重复
        freMap.put("outSeqNo", System.currentTimeMillis() + "");
        // 自由职业者身份证
        freMap.put("idno", "610324199810170022");
        // openId
        freMap.put("acctNo", "o033E6wroHNhlNaFHKOLZQ-Yl5Gc");
        // settleFee 结算金额（整数，单位：分）
        freMap.put("settleFee", 30);
        freMap.put("name", "丽丽");
        // remark
//        freMap.put("remark", "wl test");
        freMap.put("wxAppId", "wxc868392490bce7b6");
        dataMap.put("freelancers", Collections.singleton(freMap));

        log.info("dataMap:{}", JSON.toJSONString(dataMap));
        BaseDataVo vo = RsaSignDemo.createSendMsgVo(bblgProperties.getAppId(), bblgProperties.getPublicKey(),
                bblgProperties.getPrivateKey(), JSON.toJSONString(dataMap));
        BaseDataVo resp = bblgApi.settleBatch(vo);
        log.info("resp:{}", resp);

        return resp;
//        BaseDataVo baseDataVo = RsaSignDemo.decryptAndVerify(bblgProperties.getPublicKey(), bblgProperties.getPrivateKey(), JSON.toJSONString(resp));
//        return JSON.parse(baseDataVo.getData());
    }
}
