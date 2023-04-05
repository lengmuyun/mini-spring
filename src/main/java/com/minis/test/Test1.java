package com.minis.test;

import com.minis.context.ClassPathXmlApplicationContext;
import com.minis.beans.BeansException;

public class Test1 {

    public static void main(String[] args) throws BeansException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        AService aService = (AService) context.getBean("aservice");
        aService.sayHello();
    }

}
