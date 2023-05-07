package com.minis.test.web;

import com.minis.extra.HelloRequest;
import com.minis.web.RequestMapping;

public class HelloWorldBean {

    @RequestMapping("/test")
    public String doGet() {
        return "hello world for doGet";
    }

    @RequestMapping("/test1")
    public String doGet1(HelloRequest request) {
        return "hello world for doGet, name: " + request.getName() + ", address: " + request.getAddress();
    }

}
