package com.example.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName BxCtrl.java
 * @Description TODO
 * @createTime 2019年12月30日 14:55:00
 */
@Slf4j
@Controller
@RequestMapping("/send")
public class BxCtrl {

    @RequestMapping("/request")
    @ResponseBody
    public String request() throws Exception {
        /**
        * @Description 保险向平台推送保单信息
        * @Date 2019/12/30 15:00
        * @return void
        * @Author FanJiangFeng
        * @Version1.0
        * @History
        */
        //封装基础信息
        String plainData = getPlainData();

        //封装请求体
        JSONObject head=new JSONObject();
        head.put("APPID","1666666");
        head.put("transId","");
        head.put("timeStamp",new Date().getTime());
        head.put("bussinessId",UUID.randomUUID().toString().replaceAll("-",""));
        JSONObject data=new JSONObject();
        data.put("requestData",plainData);
        JSONObject req=new JSONObject();
        req.put("head",head);
        req.put("data",data);
        String reqString = req.toJSONString();

        //将reqString加密
        String requestData = getRequestData(reqString);

        //发起请求
        RestTemplate restTemplate=new RestTemplate();
        String url="http://127.0.0.1:8000/tbbx-server/tbbx/responseData";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,String> maps=new LinkedMultiValueMap<String, String>();
        maps.add("request",requestData);
        HttpEntity<MultiValueMap<String,String>> httpEntity=new HttpEntity<>(maps,headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(url, httpEntity, String.class);
        String responseData = entity.getBody();
        log.info(responseData);
        String response = decryptData(responseData);
        return response;

    }

    /**
    * @Description 封装基础信息
    * @Date 2019/12/30 16:45
    * @return
    * @Author FanJiangFeng
    * @Version1.0
    * @History
    */
    public String getPlainData(){
        //唯一标识
        String billId= UUID.randomUUID().toString().replaceAll("-","");
        //保单编号
        String bdbh="18838030468";
        //保险类型
        String insuranceType="2";
        //保险公司编号
        String insuranceCompanyId="12138";
        //保险公司名字
        String insuranceCompanyName="恕瑞玛保险";
        //保单金额
        double payMount=1314.67;
        //投保时间
        Date insureTime=new Date();
        //年份
        Integer billYear=2019;
        //月份
        Integer billMonth=12;
        Map map=new HashMap();
        map.put("billId",billId);
        map.put("bdbh",bdbh);
        map.put("insuranceType",insuranceType);
        map.put("insuranceCompanyId",insuranceCompanyId);
        map.put("insuranceCompanyName",insuranceCompanyName);
        map.put("payAmount",payMount);
        map.put("insureTime",insureTime);
        map.put("billYear",billYear);
        map.put("billMonth",billMonth);

        //唯一标识
        String billId2= UUID.randomUUID().toString().replaceAll("-","");
        //保单编号
        String bdbh2="13333333333";
        //保险类型
        String insuranceType2="2";
        //保险公司编号
        String insuranceCompanyId2="1333333";
        //保险公司名字
        String insuranceCompanyName2="无畏先锋保险公司";
        //保单金额
        double payMount2=1314.666;
        //投保时间
        Date date2=new Date();
        //年份
        Integer billYear2=2019;
        //月份
        Integer billMonth2=12;
        Map map2=new HashMap();
        map2.put("billId",billId2);
        map2.put("bdbh",bdbh2);
        map2.put("insuranceType",insuranceType2);
        map2.put("insuranceCompanyId",insuranceCompanyId2);
        map2.put("insuranceCompanyName",insuranceCompanyName2);
        map2.put("payAmount",payMount2);
        map2.put("insureTime",date2);
        map2.put("billYear",billYear2);
        map2.put("billMonth",billMonth2);

        List<Map> list=new ArrayList<>();
        list.add(map);
        list.add(map2);
        //list转jsonArray
        String jsonString = JSON.toJSONString(list);
        return jsonString;
    }


    //自己公钥
    public static final String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDe8J9NuaAsY1ki2C1sWufkYPNwZe23MmxLD98pWDK09idJUxBe+8poDBBRoT9tcEr4//lpuL2sqpEEm6s/G6rO50qlhPChpwv4CCEYWghZKLhYacpWiDkcW5SprLoElKA5+WVtDiU3dSkY09a2Z7nbxOPPN4eZU+liGM3GoW+tAQIDAQAB";
    //自己的私钥
    public static final String privateKey="MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAN7wn025oCxjWSLYLWxa5+Rg83Bl7bcybEsP3ylYMrT2J0lTEF77ymgMEFGhP21wSvj/+Wm4vayqkQSbqz8bqs7nSqWE8KGnC/gIIRhaCFkouFhpylaIORxblKmsugSUoDn5ZW0OJTd1KRjT1rZnudvE4883h5lT6WIYzcahb60BAgMBAAECgYEAhwrWVGyGm5yqV7L/AM2n/ezESWdUsU23z1gT46VrQbaBYacRGZgHqTL1h5zXIOGOaHoViHbBXiALq/DnsFiAA6bDAzHb5nbZ3nsgZjK6dhQfSP2qBIYVaqMOgENC1JxdBdfMLGFlQkzv4B/NGdcKOkrX53LD0sHi+ciIfqPaYAECQQD7nCoXIFav6gGzuUDX/Ix7WaHhKbtBqgK5BLRHe5NE1O9j3q6j+7VWaupfZMmW+4E3/L5oWz5aj/J1etxPuOJBAkEA4tRmlMhOqX0CVKu5nhSq7xYRXSGDvLzuyXDKPg387lS8wl7TRcSggUplFxlL7sFc9I66ytgtYjg6O63FDnKawQJAFhg5jfxWAG6hJaIf8peH0pElaPtcKUD/qjWiiyBr50B3oSJ93YKOAv+ygxv7o9mEGGGGau2QRS2c0fou/IU0wQJBAJ3Kj8m0RjRVSAfKpgc37WkAevfEVrY24A21rprYdDj/LYgxGtuP2u/V5tjYuh/O13Ew0PRGydtnrerSCSDxzIECQQDTH1zdUe+riUBebPunZ+pN5xSn/33gGH6ca6WVuI8SvkdSOpQnLZre1L0jjSKStHWKZwupLEg8kZr30GfPmSGH";
    //对方的公钥(平台)
    public static final String thirdPublicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOx60Jna22WwF8p9kQofWjoFxcTY3K7HS8Sv6YGAJ/dRYCVgM8vRwXRZ0jGX8HsLb1uEAH/xaC/owXhk80w/E/Jvvskb04gdjwR206SXTAn0X4t59bbryhzUrISQ/jJX0+ujJxTHzT8WsYgd152BrqHTPFRr1n7Cyuw2mXhK81qQIDAQAB";

    /**
     * @Description  解密信息并返回
     * @Date 2019/12/18 15:26
     * @return
     * @Author FanJiangFeng
     * @Version1.0
     * @History
     */
    public String decryptData(String encryptData) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(encryptData);
        JSONObject content=(JSONObject) jsonObject.get("content");
        String encryptParam=(String) content.get("encryptParam");
        String signature=(String) content.get("signature");

        PublicKey publicKey = RSAUtil.string2PublicKey(thirdPublicKey);

        //签名(对原文签名)
        PrivateKey privateKey = RSAUtil.string2PrivateKey(BxCtrl.privateKey);

        String plainData = RSAUtil.decrypt(encryptParam, privateKey, "RSA");
        log.info("反馈信息解密内容："+plainData);
        //对原文进行验签
        boolean isTrue = RSAUtil.verifySign(plainData, signature, publicKey, "SHA1withRSA");
        if(isTrue){
            log.info("验签通过");
            return plainData;

        }else {
            log.info("验签失败");
            return "";
        }
    }

    /**
    * @Description 加密方法
    * @Date 2019/12/30 16:28
    * @return
    * @Author FanJiangFeng
    * @Version1.0
    * @History
    */
    public String getRequestData(String plainData) throws Exception {
        //签名(对原文签名)
        PrivateKey privateKey = RSAUtil.string2PrivateKey(BxCtrl.privateKey);
        String signature = RSAUtil.sign(plainData, privateKey, "SHA1withRSA");
        //加密后的字符串
        PublicKey publicKey = RSAUtil.string2PublicKey(thirdPublicKey);
        String encryptParam = RSAUtil.encrypt(plainData, publicKey, "RSA");
        Map map=new LinkedHashMap();
        map.put("APPID","1666666");
        map.put("encryptParam",encryptParam);
        map.put("signature",signature);
        String requestData = JSONObject.toJSONString(map);
        return requestData;
    }

}
