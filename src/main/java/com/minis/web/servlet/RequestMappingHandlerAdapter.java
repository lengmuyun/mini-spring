package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.context.WebApplicationContext;
import com.minis.web.WebBindingInitializer;
import com.minis.web.WebDataBinder;
import com.minis.web.WebDataBinderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestMappingHandlerAdapter implements HandlerAdapter {

    private WebApplicationContext wac;
    private WebBindingInitializer webBindingInitializer;

    public RequestMappingHandlerAdapter(WebApplicationContext wac) {
        this.wac = wac;
        try {
            this.webBindingInitializer = (WebBindingInitializer) this.wac.getBean("webBindingInitializer");
        } catch (BeansException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            handleInternal(request, response, (HandlerMethod) handler);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException e) {
            e.printStackTrace();
        }
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        WebDataBinderFactory binderFactory = new WebDataBinderFactory();
        Parameter[] methodParameters = handlerMethod.getMethod().getParameters();
        Object[] methodParamObjs = new Object[methodParameters.length];
        // 对调用方法里的每一个参数，处理绑定
        for (int i = 0; i < methodParameters.length; i++) {
            Parameter methodParameter = methodParameters[i];
            Object methodParamObj = methodParameter.getType().newInstance();
            WebDataBinder wdb = binderFactory.createBinder(request, methodParamObj, methodParameter.getName());
            webBindingInitializer.initBinder(wdb);
            wdb.bind(request);
            methodParamObjs[i] = methodParamObj;
        }

        Method invocableMethod = handlerMethod.getMethod();
        Object returnObj = invocableMethod.invoke(handlerMethod.getBean(), methodParamObjs);
        response.getWriter().append(returnObj.toString());
    }

}
