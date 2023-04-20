package com.minis.test;

import com.minis.context.ClassPathXmlApplicationContext;
import com.minis.beans.BeansException;

import java.util.Map;

public class Test1 {

    public static void main(String[] args) throws BeansException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        AService aservice = (AService) ctx.getBean("aservice");
        aservice.sayHello();

        Map<String, AService> beansOfType = ctx.getBeansOfType(AService.class);
        System.out.println("AService: " + beansOfType);
    }

}
