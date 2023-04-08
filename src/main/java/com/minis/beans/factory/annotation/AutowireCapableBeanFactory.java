package com.minis.beans.factory.annotation;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.support.AbstractBeanFactory;

import java.util.ArrayList;
import java.util.List;

public class AutowireCapableBeanFactory extends AbstractBeanFactory {

    private final List<AutowiredAnnotationBeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public void addBeanPostProcessor(AutowiredAnnotationBeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    @Override
    public Object applyBeanPostProcessorBeforeInitialization(Object singleton, String beanName) {
        Object result = singleton;
        for (AutowiredAnnotationBeanPostProcessor beanPostProcessor : this.beanPostProcessors) {
            beanPostProcessor.setBeanFactory(this);
            try {
                result = beanPostProcessor.postProcessBeforeInitialization(singleton, beanName);
            } catch (BeansException e) {
                e.printStackTrace();
            }
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    @Override
    public Object applyBeanPostProcessorAfterInitialization(Object singleton, String beanName) {
        Object result = singleton;
        for (AutowiredAnnotationBeanPostProcessor beanPostProcessor : this.beanPostProcessors) {
            beanPostProcessor.setBeanFactory(this);
            try {
                result = beanPostProcessor.postProcessAfterInitialization(singleton, beanName);
            } catch (BeansException e) {
                e.printStackTrace();
            }
            if (result == null) {
                return result;
            }
        }
        return result;
    }

}
