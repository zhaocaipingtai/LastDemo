package com.example.centre;

import com.alibaba.fastjson.JSONObject;
import com.example.util.SM3Util;
import com.example.util.SM4Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.Base64Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName TouBaoCtrl.java
 * @Description TODO  投保接口翻新
 * @createTime 2020年01月06日 15:41:00
 */
@Slf4j
@Controller
public class TouBaoCtrl {

    static{
        Security.addProvider(new BouncyCastleProvider());
    }
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
        String applyno="18838030468";
        //投标企业名称
        String biddername="中心集团";
        //统一社会信用代码
        String biddercode="91320582704068740Y";
        //标段编号（密文）
        String biaoduanno="FXHdgO+ycoenm1+7VW5pvA==";
        //标段名称(密文)
        String biaoduanname="z+z7s7O6JCpKbqFSx3Zthw==";
        //保证金金额
        BigDecimal bzjamount=new BigDecimal(1000.50);
        //招标人（密文）
        String zbr="mVyNYMgI+DAPQsJgH6dIvw==";
        //招标人统一社会信用代码（密文）
        String zbrorgnum="Nxl2VeI1Dof6XAz8DfT5yQ==";
        //报文签名
//        String sign="1feeccb15045c967f5c3bacd895c4ce5ed44b5d287f9a621662e1bf403b7dc21";
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

        //对以上的json串进行签名
        String jsonString = JSONObject.toJSONString(sendRequestData);
        byte[] signHash= SM3Util.hash(jsonString.getBytes("UTF-8"));
        StringBuilder signature = new StringBuilder();
        for (byte b : signHash) {
            signature.append(byteToHexString(b));
        }
        String sign=signature.toString();
        log.info("生成签名："+sign);

        sendRequestData.put("sign",sign);
        String requestData = sendRequestData.toJSONString();

        //发送远程请求
        String url="http://127.0.0.1:7000/tbbx/uapLogin";
        JSONObject responseJSON = doPost(url, requestData,sign,appkey);
        return responseJSON;

    }



    public static JSONObject doPost(String url,String requestData,String sign,String appkey) {
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("APPID",appkey);
        headers.set("APPSECRET","123456");
        headers.set("TRAN_ID","test");
        String time = String.valueOf(new Date().getTime()) ;
        headers.set("TIMESTAMP",time);
        //对请求头进行签名
        String signs = verify(appkey, "123456", "test", time);

        headers.set("APP_SIGN",signs);
        MultiValueMap<String,String> map=new LinkedMultiValueMap<>();
        map.add("requestData",requestData);
        HttpEntity<MultiValueMap<String,String>> httpEntity=new HttpEntity<>(map,headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        return jsonObject;
    }

    /**
    * @Description 对请求头进行签名
    * @Date 2020/1/10 16:00
    * @return
    * @Author FanJiangFeng
    * @Version1.0
    * @History
    */
    public static String verify(String appid,String appsecret,String tran_id,String timestamp){
        JSONObject jo = new JSONObject();
        jo.put("APPID",appid);
        jo.put("APPSECRET",appsecret);
        jo.put("TRAN_ID",tran_id);
        jo.put("TIMESTAMP",timestamp);
        String encode = DigestUtils.md5DigestAsHex(jo.toString().getBytes());
        return encode;
    }

    public static String byteToHexString(byte ib) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0f];
        ob[1] = Digit[ib & 0X0F];
        String str = new String(ob);
        return str;
    }

}
