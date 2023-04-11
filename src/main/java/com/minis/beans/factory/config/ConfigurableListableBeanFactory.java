package com.minis.beans.factory.config;

import com.minis.beans.factory.ConfigurableBeanFactory;
import com.minis.beans.factory.ListableBeanFactory;
import com.minis.beans.factory.annotation.AutowireCapableBeanFactory;

public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory,
        ConfigurableBeanFactory {
}
