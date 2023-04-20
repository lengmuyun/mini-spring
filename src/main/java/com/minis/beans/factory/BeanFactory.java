package com.minis.beans.factory;

import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.BeansException;

public interface BeanFactory {

    Object getBean(String beanName) throws BeansException;

    boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    Class<?> getType(String name);

    void registerBeanDefinition(BeanDefinition beanDefinition);

    /**
     * 将方法 {@link BeanFactory#registerBeanDefinition(BeanDefinition)} 重命名为该方法，并调整参数
     * (好像有问题)
     */
    void registerBean(String beanName, Object obj);

}
