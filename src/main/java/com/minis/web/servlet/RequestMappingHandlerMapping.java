package com.minis.web.servlet;

import com.minis.context.WebApplicationContext;
import com.minis.web.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class RequestMappingHandlerMapping implements HandlerMapping {

    private final WebApplicationContext wac;

    private final MappingRegistry mappingRegistry = new MappingRegistry();

    public RequestMappingHandlerMapping(WebApplicationContext wac) {
        this.wac = wac;
        initMapping();
    }

    // 建立URL与调用方法和实例的映射关系, 存储在mappingRegistry中
    private void initMapping() {
        Class<?> clz = null;
        Object obj = null;
        String[] controllerNames = this.wac.getBeanDefinitionNames();
        //扫描WAC中存放的所有bean
        for (String controllerName : controllerNames) {
            try {
                clz = Class.forName(controllerName);
                obj = this.wac.getBean(controllerName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Method[] methods = clz.getDeclaredMethods();
            //检查每一个方法声明
            for (Method method : methods) {
                boolean isRequestMapping =
                        method.isAnnotationPresent(RequestMapping.class);
                //如果该方法带有@RequestMapping注解,则建立映射关系
                if (isRequestMapping) {
                    String methodName = method.getName();
                    String urlmapping =
                            method.getAnnotation(RequestMapping.class).value();

                    this.mappingRegistry.getUrlMappingNames().add(urlmapping);
                    this.mappingRegistry.getMappingObjs().put(urlmapping,
                            obj);
                    this.mappingRegistry.getMappingMethods().put(urlmapping,
                            method);
                }
            }
        }
    }

    @Override
    public HandlerMethod getHandler(HttpServletRequest request) {
        String sPath = request.getServletPath();
        if (!this.mappingRegistry.getUrlMappingNames().contains(sPath)) {
            return null;
        }
        Method method = this.mappingRegistry.getMappingMethods().get(sPath);
        Object obj = this.mappingRegistry.getMappingObjs().get(sPath);
        HandlerMethod handlerMethod = new HandlerMethod(method, obj);
        return handlerMethod;
    }

}
