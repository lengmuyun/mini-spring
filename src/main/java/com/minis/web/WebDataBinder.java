package com.minis.web;

import com.minis.beans.PropertyEditor;
import com.minis.beans.PropertyValues;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WebDataBinder {
    private Object target;
    private Class<?> clz;
    private String objectName;
    BeanWrapperImpl propertyAccessor;

    public WebDataBinder(Object target) {
        this(target, "");
    }

    public WebDataBinder(Object target, String targetName) {
        this.target = target;
        this.objectName = targetName;
        this.clz = target.getClass();
        this.propertyAccessor = new BeanWrapperImpl(this.target);
    }

    // 核心绑定方法, 将request里面的参数值绑定到目标对象的属性上
    public void bind(HttpServletRequest request) {
        PropertyValues mpvs = assignParameters(request);
        addBindValues(mpvs, request);
        doBind(mpvs);
    }

    private void doBind(PropertyValues mpvs) {
        applyPropertyValues(mpvs);
    }

    // 实际将参数值与对象属性进行绑定的方法
    private void applyPropertyValues(PropertyValues mpvs) {
        getPropertyAccessor().setPropertyValues(mpvs);
    }

    // 设置属性值的工具
    private BeanWrapperImpl getPropertyAccessor() {
        return this.propertyAccessor;
    }

    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        getPropertyAccessor().registerCustomEditor(requiredType, propertyEditor);
    }

    private PropertyValues assignParameters(HttpServletRequest request) {
        Map<String, Object> map = WebUtils.getParametersStartWith(request, "");
        return new PropertyValues(map);
    }

    private void addBindValues(PropertyValues mpvs, HttpServletRequest request) {

    }

}
