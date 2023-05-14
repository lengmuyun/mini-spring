package com.minis.test.web;

import com.minis.extra.HelloRequest;
import com.minis.web.RequestMapping;
import com.minis.web.bind.annotation.ResponseBody;

import java.util.Date;

public class HelloWorldBean {

    @RequestMapping("/test")
    public String doGet() {
        return "hello world for doGet";
    }

    @RequestMapping("/test1")
    public String doGet1(HelloRequest request) {
        return "hello world for doGet, name: " + request.getName() + ", address: " + request.getAddress();
    }


    @RequestMapping("/test7")
    @ResponseBody
    public User doTest7(User user) {
        user.setName(user.getName() + "---");
        user.setBirthday(new Date());
        return user;
    }

    public class User {
        private String name;
        private Date birthday;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }
    }

}
