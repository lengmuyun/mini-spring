package com.minis.context;

import com.minis.beans.BeansException;
import com.minis.beans.factory.config.BeanFactoryPostProcessor;
import com.minis.beans.factory.config.ConfigurableListableBeanFactory;
import com.minis.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractApplicationContext implements ApplicationContext {

    private Environment environment;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    private long startupDate;
    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();
    private ApplicationEventPublisher applicationEventPublisher;

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        return getBeanFactory().getBean(beanName);
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return beanFactoryPostProcessors;
    }

    public void refresh() {
        postProcessBeanFactory(getBeanFactory());
        registerBeanPostProcessors(getBeanFactory());
        initApplicationEventPublisher();
        onRefresh();
        registerListeners();
        finishRefresh();
    }

    abstract void registerListeners();
    abstract void initApplicationEventPublisher();
    abstract void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory);
    abstract void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory);
    abstract void onRefresh();
    abstract void finishRefresh();

    @Override
    public String getApplicationName() {
        return "";
    }
    @Override
    public long getStartupDate() {
        return this.startupDate;
    }
    @Override
    public abstract ConfigurableListableBeanFactory getBeanFactory() throws
            IllegalStateException;
    @Override
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }
    @Override
    public void close() {
    }
    @Override
    public boolean isActive(){
        return true;
    }

}
