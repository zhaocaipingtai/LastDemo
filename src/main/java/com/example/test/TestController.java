package com.example.test;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName TestController.java
 * @Description TODO
 * @createTime 2020年01月10日 10:34:00
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/one")
    @ResponseBody
    public User one(@RequestParam("phone") String phone, @RequestParam("address") String address){
        System.out.println("手机号："+phone+"  地址："+address);
        User user=new User();
        user.setId("18838030468");
        user.setName("樊江锋");
        user.setAge(24);
        return user;
    }
}
