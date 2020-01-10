package com.example.centre;

import com.alibaba.fastjson.JSONObject;
import com.example.util.SM3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.centre.TouBaoCtrl.byteToHexString;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName HybhCtrl.java
 * @Description TODO 还原保函接口
 * @createTime 2020年01月08日 10:34:00
 */
@Slf4j
@Controller
public class HybhCtrl {

    /**
    * @Description 还原保函接口
    * @Date 2020/1/8 10:35
    * @return
    * @Author FanJiangFeng
    * @Version1.0
    * @History
    */
    @RequestMapping("/baohanrestore")
    @ResponseBody
    public JSONObject baohanrestore() throws Exception{
        //appkey
        String appkey="001";
        //timestamp
        long time = new Date().getTime();
        Date times=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = format.format(times);
        //applyno 业务流水号
        String applyno="18838030468";
        //开标时间
        long kaibiao=new Date().getTime();
        Date date=new Date(kaibiao);
        String kaibiaotime = format.format(date);
        //加密的key
        String key="169b909483f5822975d316b6676b0a0a";
        //加密的iv
        String iv="af875a78d67a85ab68f5d87f98";
        JSONObject sendRequest=new JSONObject();
        sendRequest.put("appkey",appkey);
        sendRequest.put("timestamp",timestamp);
        sendRequest.put("applyno",applyno);
        sendRequest.put("kaibiaotime",kaibiaotime);
        sendRequest.put("key",key);
        sendRequest.put("iv",iv);

        //对以上的json串进行签名
        String jsonString = JSONObject.toJSONString(sendRequest);
        byte[] signHash= SM3Util.hash(jsonString.getBytes("UTF-8"));
        StringBuilder signature = new StringBuilder();
        for (byte b : signHash) {
            signature.append(byteToHexString(b));
        }
        String sign=signature.toString();
        log.info("生成签名："+sign);
        sendRequest.put("sign",sign);
        String requestData = JSONObject.toJSONString(sendRequest);

        //发送远程请求
        String url="http://127.0.0.1:7000/tbbx/letter";
        JSONObject responseJSON =TouBaoCtrl.doPost(url, requestData,sign,appkey);
        return responseJSON;

    }
}
