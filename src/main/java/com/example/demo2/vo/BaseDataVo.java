package com.example.demo2.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hanp
 * @title: BaseDataVo
 * @description: TODO
 * @date 2022-03-03 11:15
 */
@Data
public class BaseDataVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 签名
     */
    private String sign;
    /**
     * 签名类型
     */
    private String signType;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 随机字符串
     */
    private String nonceStr;
    /**
     * 请求id
     */
    private String reqMsgId;
    /**
     * 平台编码
     */
    private String appid;
    /**
     * 响应码
     */
    private String code;
    /**
     * 信息
     */
    private String message;
    /**
     * 业务数据
     */
    private String data;
    /**
     * 验签成功/失败
     */
    private Boolean verify;

}
