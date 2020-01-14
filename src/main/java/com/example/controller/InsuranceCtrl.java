package com.example.controller;
import com.alibaba.fastjson.JSONObject;
import com.example.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;


import org.springframework.context.annotation.Configuration;

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

import java.io.IOException;
import java.math.BigDecimal;
import java.security.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/tbbx")
@Configuration
public class InsuranceCtrl {


    //自己的私钥
    public static final String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJZRkNwYUdBnKjIfE+BvrwhOKs0ErKHhb4KisYFrCtX0tOvZsmnvEdXGZa0g7oQLXD1GYEk5UZWxH+NUujQxYBQibW9XdGYogYs0wISkLeAnsno98H/w+i/mRLSD2UrS7cdA/OSeroOQsQ0fYlJJmZpZ6NldekocL48yOGRimKatAgMBAAECgYB4z5VdTy5yEHgZUGDpQNmsEybTH66fbE/y7k87dIyA6Ot/oreB7GKpLYsKWi1YcsjvdmBseWTYqkK2sqH00LomORqw4DYyCC+UtRTiBF7ayF78fAFQJ+aJTYxTxBX/T5C0qIcZFWJM9kodpsATeAnB644vgK99skHqb1ITT3HoAQJBAMj7pPaDrrfOhnsr+ni20nKHBJEf7j7v6y+ytBeCn1NDJiiaN1W+COlZMs3eRVciAZFMHUxsT61UVXEn3oTNAFUCQQC/d38grDIBLW89RCy6269ESUwsW90VAGLxkOgUuoCziQO7tghJRdpW+nBSTfRpdcKEaZb6ggKq5Gr5EmBgagT5AkAYDu93RtoSTJmSgvgvnQriBMGKMb1OMWAAzK58Jfm2eNJHh/ZZwC7G6BOJyzKXtI1lfCwBqvF6uR6lNfWQBg4lAkEAhGiVJ/ktU5ciGJJvBIIsQOMXpI2a5I0x37DAbqVTn9UfGc8XB+Ugy4nB54k1f0EK1htXZ8JFKf1w5PKKZ+KpsQJBAKwOhPKOS1NQK0KQ5Hl64yIxFxUDjXteZJ34SfWsY6MqssrifXt11HIo3c6rAkfwdMDPTJHQTLKnIr1xIcLny90=";
    //对方的公钥
    public static final String thirdPublicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOx60Jna22WwF8p9kQofWjoFxcTY3K7HS8Sv6YGAJ/dRYCVgM8vRwXRZ0jGX8HsLb1uEAH/xaC/owXhk80w/E/Jvvskb04gdjwR206SXTAn0X4t59bbryhzUrISQ/jJX0+ujJxTHzT8WsYgd152BrqHTPFRr1n7Cyuw2mXhK81qQIDAQAB";


    /**
     * @Description 招采初始化页面
     * @Date 2019/12/16 15:54
     * @return
     * @Author FanJiangFeng
     * @Version1.0
     * @History
     */
    @RequestMapping("/enterPage")
    public String yemian(){
        return "test";
    }

    /**
     * @title
     * @description 招采平台进入保险平台ajax入口
     * @author FanJiangFeng
     * @updateTime 2019/12/10 14:19
     * @throws
     */
    @RequestMapping("/insurance")
    @ResponseBody
    public JSONObject sendAsk() throws Exception{


        String appid = "18838030468";
        //##########第一步，封装基础信息##########
        String plainData = getPlainData();

        //########加密和签名##########
        String requestData = getRequestData(plainData);

        //###############第三步，发送远程请求############
        String responseData = sendPostReq(requestData,"http://127.0.0.1:8000/tbbx-server/tbbx/insurance/enter");

        //############第四步，得到反馈信息##############
        String contentData = getContentData(responseData);
        if("false".equals(contentData)){
            JSONObject jsonObject = JSONObject.parseObject(responseData);
            return jsonObject;
        }


        //#############第五步，解密反馈信息并返回############
        String decryptString = decryptData(contentData);
        JSONObject jsonObject = JSONObject.parseObject(decryptString);
        return jsonObject;

    }

    @RequestMapping("/bidopen")
    @ResponseBody
    public JSONObject sendBidOpen() throws Exception{


        String appid = "18838030468";
        //##########第一步，封装基础信息##########
        String plainData = getBidOpenData();

        //########加密和签名##########
        String requestData = getRequestData(plainData);

        //###############第三步，发送远程请求############
        String responseData = sendPostReq(requestData,"http://127.0.0.1:8000/tbbx-server/tbbx/insurance/bidopen");

        //############第四步，得到反馈信息##############
        String contentData = getContentData(responseData);
        if("false".equals(contentData)){
            JSONObject jsonObject = JSONObject.parseObject(responseData);
            return jsonObject;
        }


        //#############第五步，解密反馈信息并返回############
        String decryptString = decryptData(contentData);
        JSONObject jsonObject = JSONObject.parseObject(decryptString);
        return jsonObject;

    }

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
        String encryptParam=(String) jsonObject.get("encryptParam");
        String signature=(String) jsonObject.get("signature");

        //解密后的内容
        PublicKey publicKey = RSAUtil.string2PublicKey(thirdPublicKey);

        //签名(对原文签名)
        PrivateKey privateKey = RSAUtil.string2PrivateKey(InsuranceCtrl.privateKey);

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
     * @Description  得到反馈信息
     * @Date 2019/12/18 15:22
     * @return
     * @Author FanJiangFeng
     * @Version1.0
     * @History
     */
    public String getContentData(String responseData){
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        Integer status =  Integer.parseInt(jsonObject.getString("status"));
        String message = (String) jsonObject.get("message");
        if(status==1000){
            JSONObject jsonObject1=(JSONObject) jsonObject.get("content");
            String content = jsonObject1.toJSONString();
            return content;
        }else if(status==500){
            return "false";
        }
        return "";
    }

    /**
     * @Description 加密和签名公共方法
     * @Date 2019/12/18 15:18
     * @return
     * @Author FanJiangFeng
     * @Version1.0
     * @History
     */
    public Map jmqm(String jcxx) throws Exception {
        PublicKey publicKey = RSAUtil.string2PublicKey(thirdPublicKey);
        //加密后的字符串
        String encryptParam = RSAUtil.encrypt(jcxx, publicKey, "RSA");
        //签名(对原文签名)
        PrivateKey privateKey = RSAUtil.string2PrivateKey(InsuranceCtrl.privateKey);
        String signature = RSAUtil.sign(jcxx, privateKey, "SHA1withRSA");
        Map map=new HashMap();
        map.put("encryptParam",encryptParam);
        map.put("signature",signature);
        return map;
    }

    public String getRequestData(String plainData) throws Exception {
        //签名(对原文签名)
        PrivateKey privateKey = RSAUtil.string2PrivateKey(InsuranceCtrl.privateKey);
        String signature = RSAUtil.sign(plainData, privateKey, "SHA1withRSA");

        //加密后的字符串
        PublicKey publicKey = RSAUtil.string2PublicKey(thirdPublicKey);
        String encryptParam = RSAUtil.encrypt(plainData, publicKey, "RSA");
        Map map=new LinkedHashMap();
        map.put("APPID","18838030468");
        map.put("encryptParam",encryptParam);
        map.put("signature",signature);
        String requestData = JSONObject.toJSONString(map);

        return requestData;
    }

    /**
     * @Description 封装基础信息
     * @Date 2019/12/16 15:53
     * @return String  json串
     * @Author FanJiangFeng
     * @Version1.0
     * @History
     */
    public String getPlainData() throws Exception {
        //基础信息
        String appid = "18838030468";        //接入机构ID唯一标识
        String transId="Test";               //交易名称
        long time = new Date().getTime();    //时间戳
        String businessId=this.getUUID();    //业务流水号 （这里UUID模拟）

        //业务字段
        //唯一编号（代表投标人在投标业务中的唯一标识）
        String ddbh="5c1b1691958a41878dbfe55a931e2165";
        //项目名称
        String projectName="测试项目-大刚";
        //项目编号
        String projectCode="No.1";
        //招标项目名称
        String tenderProjectName="招标测试项目";
        //招标项目编号
        String tenderProjectCode="Number.1";
        //标段（包）名称
        String bidSectionName="测试标段（包）";
        //标段（包）编号
        String bidSectionCode="bd_No.1";
        //投标保证金金额
        BigDecimal tbbzjje=new BigDecimal("100000.25");
        //开标日期
        Date openTime=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
        String bidOpenTime = simpleDateFormat.format(openTime);
        //招标人名称
        String tendererName="樊江锋";
        //招标人社会信用代码
        String tendererShxydm="crxylh";
        //招标人证件类型   （选填）
        String tendererCodeType="身份证";
        //招标人证件号码    （选填）
        String tendererCode="410622199703045010";
        //招标人地址
        String tendererAddress="";
        //招标人联系电话
        String tendererPhoneNumber="";
        //投标人名称
        String bidderName="袁梦阳";
        //投标人社会信用代码
        String bidderShxydm="crxyhhdl";
        //投标人证件类型   （选填）
        String bidderCodeType="身份证";
        //投标人证件号码   （选填）
        String bidderCode="410633844099543761";
        //投保申请日期
        Date format=new Date();
        String tbsqrq = simpleDateFormat.format(format);

        //把基础信息和业务字段放到map中
        Map head=new HashMap();
        head.put("appid",appid);
        head.put("transId",transId);
        head.put("time",time);
        head.put("businessId",businessId);
        Map data=new HashMap();
        data.put("ddbh",ddbh);
        data.put("projectName",projectName);
        data.put("projectCode",projectCode);
        data.put("tenderProjectName",tenderProjectName);
        data.put("tenderProjectCode",tenderProjectCode);
        data.put("bidSectionName",bidSectionName);
        data.put("bidSectionCode",bidSectionCode);
        data.put("tbbzjje",tbbzjje);
        data.put("bidOpenTime",bidOpenTime);
        data.put("tendererName",tendererName);
        data.put("tendererShxydm",tendererShxydm);
        data.put("tendererCodeType",tendererCodeType);
        data.put("tendererCode",tendererCode);
        data.put("tendererAddress",tendererAddress);
        data.put("tendererPhoneNumber",tendererPhoneNumber);
        data.put("bidderName",bidderName);
        data.put("bidderShxydm",bidderShxydm);
        data.put("bidderCodeType",bidderCodeType);
        data.put("bidderCode",bidderCode);
        data.put("tbsqrq",tbsqrq);
        Map allMessage=new HashMap();
        allMessage.put("head",head);
        allMessage.put("data",data);

        //将map转成json
        String reqJson = JSONObject.toJSONString(allMessage);

        return reqJson;
    }

    public String getBidOpenData() throws Exception {
        //基础信息
        String appid = "18838030468";        //接入机构ID唯一标识
        String transId="Test";               //交易名称
        long time = new Date().getTime();    //时间戳
        String businessId="5c1b1691958a41878dbfe55a931e2165";    //业务流水号 （这里UUID模拟）

        //业务字段
        //项目名称
        String projectName="测试项目";
        //项目编号
        String projectCode="No.1";
        //招标项目名称
        String tenderProjectName="招标测试项目";
        //招标项目编号
        String tenderProjectCode="Number.1";
        //标段（包）名称
        String bidSectionName="测试标段（包）";
        //标段（包）编号
        String bidSectionCode="bd_No.1";
        //开标状态及结果
        String kbztjg="Y";
        //中标人名称
        String bidWinnerName="袁梦阳";
        //中标人社会信用代码
        String bidWinnerShxydm="crxyhhdl";
        //中标人证件类型   （选填）
        String bidWinnerCodeType="身份证";
        //中标人证件号码   （选填）
        String bidWinnerCode="410633844099543761";
        //实际开标时间
        Date sjkbsj = new Date();
        //数据同步日期
        Date sjtbrq = new Date();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");

        String str_sjkbrq = sdf.format(sjkbsj);

        String str_sjtbrq = sdf.format(sjtbrq);

        //把基础信息和业务字段放到map中
        Map head=new HashMap();
        head.put("appid",appid);
        head.put("transId",transId);
        head.put("time",time);
        head.put("businessId",businessId);
        Map data=new HashMap();
        data.put("projectName",projectName);
        data.put("projectCode",projectCode);
        data.put("tenderProjectName",tenderProjectName);
        data.put("tenderProjectCode",tenderProjectCode);
        data.put("bidSectionName",bidSectionName);
        data.put("bidSectionCode",bidSectionCode);
        data.put("kbztjg",kbztjg);
        data.put("bidWinnerName",bidWinnerName);
        data.put("bidWinnerShxydm",bidWinnerShxydm);
        data.put("bidWinnerCodeType",bidWinnerCodeType);
        data.put("bidWinnerCode",bidWinnerCode);
        data.put("sjkbsj",str_sjkbrq);
        data.put("sjtbrq",str_sjtbrq);
        Map allMessage=new HashMap();
        allMessage.put("head",head);
        allMessage.put("data",data);

        //将map转成json
        String reqJson = JSONObject.toJSONString(allMessage);

        return reqJson;
    }




    /**
     * @Description  发送远程请求，进入投标保险首页
     * @Date 2019/12/16 15:57
     * @return
     * @Author FanJiangFeng
     * @Version1.0
     * @History
     */
    public String sendPostReq(String requestData,String url) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("requestData",requestData);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
        String responseData = response.getBody();
        return responseData;
    }



    public String getUUID() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        String replace = s.replace("-", "");
        System.out.println("随机数：" + replace);
        return replace;
    }
}
