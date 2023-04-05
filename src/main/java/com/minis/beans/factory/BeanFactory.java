package com.minis.beans.factory;

import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.BeansException;

public interface BeanFactory {

    Object getBean(String beanName) throws BeansException;

    boolean containsBean(String name);

    boolean isSingleton(String name);

    boolean isPrototype(String name);

    Class<?> getType(String name);

    void registerBean(String beanName, Object obj);

    void registerBean(BeanDefinition beanDefinition);

    void refresh();

}
