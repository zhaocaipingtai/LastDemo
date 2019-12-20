package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName BdStatusCtrl.java
 * @Description TODO
 * @createTime 2019年12月19日 14:13:00
 */
@Slf4j
@Controller
@RequestMapping("/tbbx")
public class BdStatusCtrl {

    @Autowired
    InsuranceCtrl insuranceCtrl;

    /**
    * @Description 保险平台推送过来保单状态信息并反馈信息
    * @Date 2019/12/19 16:18
    * @param 接收的数据  加入github版本控制
    * @return   json
    * @Author FanJiangFeng
    * @Version1.0
    * @History
    */
    @RequestMapping(value = "/responseStatus",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String responseStatus(@RequestParam String message) throws Exception {
        String decryptData = insuranceCtrl.decryptData(message);
        JSONObject jsonObject = JSONObject.parseObject(decryptData);
        log.info("解密后的保单状态信息："+jsonObject);
        //######把保单状态信息插入招采的数据库########
        //反馈信息
        JSONObject response=new JSONObject();
        response.put("flag","1");
        response.put("remark","发送成功");
        String jsonString = JSONObject.toJSONString(response);
        //反馈信息加密
        String requestData = insuranceCtrl.getRequestData(jsonString);
        return requestData;
    }
}
