package com.example.test;

/**
 * @author FanJiangFeng
 * @version 1.0.0
 * @ClassName User.java
 * @Description TODO
 * @createTime 2020年01月10日 10:36:00
 */
public class User {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private Integer age;
}
