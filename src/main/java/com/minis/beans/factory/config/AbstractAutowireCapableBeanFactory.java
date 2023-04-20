package com.minis.beans.factory.config;

import com.minis.beans.BeansException;
import com.minis.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import com.minis.beans.factory.support.AbstractBeanFactory;

import java.util.ArrayList;
import java.util.List;

public class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private final List<AutowiredAnnotationBeanPostProcessor> beanPostProcessors = new ArrayList<>();

    public void addBeanPostProcessor(AutowiredAnnotationBeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object singleton, String beanName) {
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
    public Object applyBeanPostProcessorsAfterInitialization(Object singleton, String beanName) {
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
