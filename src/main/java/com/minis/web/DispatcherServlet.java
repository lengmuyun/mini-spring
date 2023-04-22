package com.minis.web;

import com.minis.core.ClassPathXmlResource;
import com.minis.core.Resource;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DispatcherServlet extends HttpServlet {

    private Map<String, MappingValue> mappingValues;
    private Map<String, Class<?>> mappingClz = new HashMap<>();
    private Map<String, Object> mappingObjs = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        URL xmlPath = null;
        try {
            xmlPath = this.getServletConfig().getServletContext().getResource(contextConfigLocation);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Resource rs = new ClassPathXmlResource(xmlPath);
        XmlConfigReader reader = new XmlConfigReader();
        mappingValues = reader.loadConfig(rs);
        refresh();
    }

    // 对所有的mappingValues中注册的类进行实例化，默认构造函数
    protected void refresh() {
        for (Map.Entry<String,MappingValue> entry : mappingValues.entrySet()) {
            String id = entry.getKey();
            String className = entry.getValue().getClz();
            Object obj = null;
            Class<?> clz = null;
            try {
                clz = Class.forName(className);
                obj = clz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mappingClz.put(id, clz);
            mappingObjs.put(id, obj);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sPath = req.getServletPath(); //获取请求的path
        if (this.mappingValues.get(sPath) == null) {
            return;
        }

        Class<?> clz = this.mappingClz.get(sPath); //获取bean类定义
        Object obj = this.mappingObjs.get(sPath);  //获取bean实例
        String methodName = this.mappingValues.get(sPath).getMethod(); //获取调用方法名
        Object objResult = null;
        try {
            Method method = clz.getMethod(methodName);
            objResult = method.invoke(obj); //方法调用
        } catch (Exception e) {
        }
        //将方法返回值写入response
        resp.getWriter().append(objResult.toString());
    }

}
