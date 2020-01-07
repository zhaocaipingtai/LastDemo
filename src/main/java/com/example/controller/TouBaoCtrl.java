package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.util.SM4Util;
import org.apache.commons.codec.binary.Base64;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName TouBaoCtrl.java
 * @Description TODO  投保接口翻新
 * @createTime 2020年01月06日 15:41:00
 */
@Controller
public class TouBaoCtrl {

    /**
    * @Description 投保接口
    * @Date 2020/1/6 15:43
    * @return
    * @Author FanJiangFeng
    * @Version1.0
    * @History
    */
    @RequestMapping("/baohanapply")
    @ResponseBody
    public JSONObject baohanapply() throws Exception{
        //appkey
        String appkey="001";
        //请求时间
        long time=new Date().getTime();
        Date times=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = format.format(times);
        //业务流水号
        String applyno= UUID.randomUUID().toString().replaceAll("-","");
        //投标企业名称
        String biddername="招采集团";
        //统一社会信用代码
        String biddercode="91320582704068740Y";
        //标段编号（密文）
        String biaoduanno="3cVYmS+aKfNVGcEtGFSFycOJqqoPUwEQp1mga2xliUE=";
        //标段名称(密文)
        String biaoduanname="zdxk51cpJ9SWylFq/J8gGfrgazqW8+F9YhRDp9DONvo=";
        //保证金金额
        BigDecimal bzjamount=new BigDecimal(1000.50);
        //招标人（密文）
        String zbr="/kdDw4lH0u4TMTRNEV8iHhDgpdGZbpy2OLrBtexp0d4=";
        //招标人统一社会信用代码（密文）
        String zbrorgnum="uL6aFw3t4xThWQUpFUJxRyUZomxIiWY9XTbFnYKZ1Vw=";
        //报文签名
        String sign="1feeccb15045c967f5c3bacd895c4ce5ed44b5d287f9a621662e1bf403b7dc21";
        JSONObject sendRequestData=new JSONObject();
        sendRequestData.put("appkey",appkey);
        sendRequestData.put("timestamp",timestamp);
        sendRequestData.put("applyno",applyno);
        sendRequestData.put("biddername",biddername);
        sendRequestData.put("biddercode",biddercode);
        sendRequestData.put("biaoduanno",biaoduanno);
        sendRequestData.put("biaoduanname",biaoduanname);
        sendRequestData.put("bzjamount",bzjamount);
        sendRequestData.put("zbr",zbr);
        sendRequestData.put("zbrorgnum",zbrorgnum);
        sendRequestData.put("sign",sign);
        String requestData = sendRequestData.toJSONString();

        //发送远程请求
        String url="http://127.0.0.1:7000/tbbx/toubao";
        JSONObject responseJSON = doPost(url, requestData);
        return responseJSON;

    }

    public static void main(String[] args) throws Exception {

        String testStr="/24lmRsOYcrPMMyKxcRXVw==";
        byte[] bKey = SM4Util.generateKey();
//        byte[] sm4 = SM4Util.encrypt_Ecb_Padding(bKey,testStr.getBytes("UTF-8"));
//        String encData = Base64.encodeBase64String(sm4);
//        System.out.println("密文：" + encData);
        byte[] dd = SM4Util.decrypt_Ecb_Padding(bKey, Base64.decodeBase64(testStr));
        String datainfo = new String(dd, "UTF-8");
        System.out.println("解密后的原文：" + datainfo);
    }


    public JSONObject doPost(String url,String requestData) {
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,String> map=new LinkedMultiValueMap<>();
        map.add("requestData",requestData);
        HttpEntity<MultiValueMap<String,String>> httpEntity=new HttpEntity<>(map,headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        return jsonObject;
    }
}
