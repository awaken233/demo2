package com.example.demo2.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Map;
import java.util.UUID;

/**
 * 对接平RSA签名示例
 */
public class RsaSignDemo {


    /**
     * 生成发送数据包
     * @param appId
     * @param bbxPublicKey 奔奔灵工公钥
     * @param myPrivateKey 对接方的私钥
     * @param data 需要发送请求的业务数据（未加密）
     * @return
     * @throws Exception
     */
    public static String createSendMsg(String appId,String bbxPublicKey,String myPrivateKey,String data) throws Exception {
        BaseDataVo vo = new BaseDataVo();
        vo.setAppid(appId);
        vo.setNonceStr(RandomStringUtils.randomAlphanumeric(20));
        vo.setReqMsgId(UUID.randomUUID().toString().replace("-",""));
        vo.setSignType("RSA");
        vo.setTimestamp(String.valueOf(System.currentTimeMillis()));
        String encryptData = MyRSA.encryptByPublicKey( data,bbxPublicKey);//使用奔奔灵工公钥进行加密
        vo.setData(encryptData);
        vo.setVerify(null);
        String signData = MyRSA.sign(JSON.toJSONString(vo),myPrivateKey);//使用我的私钥进行签名,对所有数据进行签名
        vo.setSign(signData);
        //vo.setData(encryptData);
        return JSON.toJSONString(vo);
    }

    public static BaseDataVo createSendMsgVo(String appId,String bbxPublicKey,String myPrivateKey,String data) throws Exception {
        BaseDataVo vo = new BaseDataVo();
        vo.setAppid(appId);
        vo.setNonceStr(RandomStringUtils.randomAlphanumeric(20));
        vo.setReqMsgId(UUID.randomUUID().toString().replace("-",""));
        vo.setSignType("RSA");
        vo.setTimestamp(String.valueOf(System.currentTimeMillis()));
        String encryptData = MyRSA.encryptByPublicKey( data,bbxPublicKey);//使用奔奔灵工公钥进行加密
        vo.setData(encryptData);
        vo.setVerify(null);
        String signData = MyRSA.sign(JSON.toJSONString(vo),myPrivateKey);//使用我的私钥进行签名,对所有数据进行签名
        vo.setSign(signData);
        return vo;
    }

    /**
     * 收到数据后进行解密验签（包含回调）
     * 解密&验签
     * @param publicKey 使用奔奔灵工平台公钥进行验签
     * @param privateKey 使用对接方的私钥进行解密
     * @param data
     * @return
     */
    public static BaseDataVo  decryptAndVerify(String publicKey, String privateKey, String data) throws Exception {
        BaseDataVo vo = JSONObject.parseObject(data, BaseDataVo.class);
        String signData = vo.getSign();
        vo.setSign(null);//签名数据需要移除掉，不然会导致验签失败
        boolean verify = MyRSA.verify(JSON.toJSONString(vo),publicKey, signData); //使用奔奔灵工平台公钥进行验签，除sign外的所有数据进行验签
        vo.setVerify(verify);
        if(verify) {//验签失败不进行解密
            String decrypt = MyRSA.decryptByPrivateKey(JSON.toJSONString(vo.getData()), privateKey);//使用我的私钥进行解密
            vo.setData(decrypt);
        }
        return vo;
    }


    /**
     * 生成秘钥
     * 将私钥保存，公钥上传到奔奔灵工平台
     * @throws Exception
     */
    public static void getRsaKey() throws Exception {
        Map<String, String> keyMap = MyRSA.genKeyPair();
        String publicKey = MyRSA.getPublicKey(keyMap);
        String privateKey = MyRSA.getPrivateKey(keyMap);
        System.out.println("公钥："+publicKey);
        System.out.println("私钥："+privateKey);
    }

    /**
     * 发包收包模拟测试
     * @param args
     */
    public static void main(String[] args) {
        try {

            String appId = "appId123456123456";
            Map<String, String> keyMap = MyRSA.genKeyPair();
            String bbxPrivateKey = MyRSA.getPrivateKey(keyMap);//模拟平台私钥
            String bbxPublicKey = MyRSA.getPublicKey(keyMap); //模拟平台公钥
            System.out.println("模拟平台私钥==>>  "+bbxPrivateKey);
            System.out.println("模拟平台公钥==>>  "+bbxPublicKey);
            Map<String, String>  keyMap2 = MyRSA.genKeyPair();
            String myPrivateKey = MyRSA.getPrivateKey(keyMap2);//模拟我的私钥
            String myPublicKey = MyRSA.getPublicKey(keyMap2);//模拟我的公钥
            System.out.println("模拟我的私钥==>>  "+myPrivateKey);
            System.out.println("模拟我的公钥==>>  "+myPublicKey);
            //发送请求数据
            String data = "{\"itemId\":\"932235285394247680\",\"businessId\":\"923947685818540032\",\"taskId\":\"951522764807618560\"}";
            String sendMSg = createSendMsg(appId,bbxPublicKey,myPrivateKey,data);
            System.out.println("发送请求数据==>>  "+sendMSg);
            //奔奔灵工接收验签
            System.out.println("奔奔灵工收到加密数据==>>  "+sendMSg);
            BaseDataVo vo = decryptAndVerify(myPublicKey,bbxPrivateKey,sendMSg);//平台验签解密key值与对接方相反
            System.out.println("奔奔灵工收到解密数据==>>  "+JSON.toJSONString(vo));
            System.out.println("奔奔灵工验签结果==>>  "+vo.getVerify());

            //返回数据
            String resData = "{\"rows\":[{\"companyName\":\"测试企业rxt02\",\"commissionHigh\":200.00,\"checkRequire\":\"01\",\"parkId\":2,\"commissionLow\":100.00,\"modifyTime\":1646902306000,\"id\":175,\"deptId\":190,\"taskDesc\":\"我公司需要开展工程施工/勘察服务项目，现面向自由职业者发布工程勘察勘探任务；任务不限制完成需要自由职业者提供工程施工/勘察服务等服务，诚邀具备工程施工/勘察的自由职业者\",\"itemId\":64,\"checkPeriod\":\"1\",\"itemTypeName\":\"房地产/建筑\",\"serviceTypeName\":\"工程施工/勘察服务\",\"status\":\"02\",\"itemType\":\"11\",\"parkName\":\"uat测试园区\",\"issueTime\":1646902305000,\"itemTitle\":\"项目发布测试2\",\"taskTypeName\":\"工程勘察勘探\",\"taskType\":\"172\",\"creditCode\":\"1111111\",\"verifyStandard\":\"按月完成项目计划规定范围内各项工作内容，提交符合行业标准和项目目标的工作成果，包括测试及验收报告等\",\"recruitType\":\"2\",\"deliveryType\":\"2\",\"commissionType\":\"01\",\"createTime\":1646902305000,\"createUser\":0,\"taskTitle\":\"导出自由职业者项目22222\",\"itemPeriod\":\"04\",\"taskId\":\"951522764807618560\"}],\"total\":1}";
            BaseDataVo bbxvo = new BaseDataVo();
            BeanUtils.copyProperties(vo,bbxvo);
            String encryptResultData = MyRSA.encryptByPublicKey(resData,myPublicKey);
            String signRestltData = MyRSA.sign(resData,bbxPrivateKey);
            bbxvo.setSign(signRestltData);
            bbxvo.setData(encryptResultData);
            bbxvo.setCode("200");
            bbxvo.setMessage("操作成功");
            System.out.println("奔奔灵工返回加密数据==>>  "+JSON.toJSONString(bbxvo));
            bbxvo = decryptAndVerify(bbxPublicKey,myPrivateKey,JSON.toJSONString(bbxvo));
            System.out.println("对接方解密奔奔灵工返回数据==>>  "+JSON.toJSONString(bbxvo));
            System.out.println("对接方验签结果==>>  "+vo.getVerify());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
