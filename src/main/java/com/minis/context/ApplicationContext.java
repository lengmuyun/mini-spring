package com.minis.context;

import com.minis.beans.factory.ConfigurableBeanFactory;
import com.minis.beans.factory.ListableBeanFactory;
import com.minis.core.env.EnvironmentCapable;

public interface ApplicationContext
        extends EnvironmentCapable, ListableBeanFactory, ConfigurableBeanFactory,
        ApplicationEventPublisher{
}
