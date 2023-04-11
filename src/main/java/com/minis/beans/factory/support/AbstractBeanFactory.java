package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry
        implements BeanFactory, BeanDefinitionRegistry {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private List<String> beanDefinitionNames = new ArrayList<>();
    private Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    public AbstractBeanFactory() {
    }

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (BeansException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        //先尝试直接拿Bean实例
        Object singleton = this.getSingleton(beanName);
        //如果此时还没有这个Bean的实例，则获取它的定义来创建实例
        if (singleton == null) {
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition == null) {
                    throw new BeansException("Beans " + beanName + " Not Exists");
                } else {
                    //获取Bean的定义
                    try {
                        // singleton = Class.forName(beanDefinition.getClassName()).newInstance();
                        singleton = createBean(beanDefinition);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    //注册Bean实例
                    this.registerSingleton(beanName, singleton);
                    // 进行beanPostProcessor处理
                    // step 1: postProcessBeforeInitialization
                    applyBeanPostProcessorsBeforeInitialization(singleton, beanName);
                    // step 2: init-method
                    if (beanDefinition.getInitMethodName() != null && !beanDefinition.getInitMethodName().equals("")) {
                        invokeInitMethod(beanDefinition, singleton);
                    }
                    // step 3: postProcessAfterInitialization
                    applyBeanPostProcessorsAfterInitialization(singleton, beanName);
                }
            }
        }
        return singleton;
    }

    protected void invokeInitMethod(BeanDefinition beanDefinition, Object obj) {
        // TODO 这个clz不对吧
        Class<?> clz = beanDefinition.getClass();
        Method method;
        try {
            method = clz.getMethod(beanDefinition.getInitMethodName());
            method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean containsBean(String name) {
        return containsSingleton(name);
    }

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition bd) {
        this.beanDefinitionMap.put(name, bd);
        this.beanDefinitionNames.add(name);
        if (!bd.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        // 创建毛胚bean实例
        Object obj = doCreateBean(beanDefinition);
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);
        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        // 完善bean,主要是处理属性
        populateBean(beanDefinition, clz, obj);
        return obj;
    }

    private Object doCreateBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        Object obj = null;
        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        // 处理构造参数
        ConstructorArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();
        // 如果有参数
        if (!argumentValues.isEmpty()) {
            Class<?>[] paramTypes = new Class[argumentValues.getArgumentCount()];
            Object[] paramValues = new Object[argumentValues.getArgumentCount()];
            for (int i=0; i<argumentValues.getArgumentCount(); i++) {
                ConstructorArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                if ("String".equals(argumentValue.getType())) {
                    paramTypes[i] = String.class;
                    paramValues[i] = argumentValue.getValue();
                } else if ("Integer".equals(argumentValue.getType())) {
                    paramTypes[i] = Integer.class;
                    paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                } else if ("int".equals(argumentValue.getType())) {
                    paramTypes[i] = int.class;
                    paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                } else {
                    paramTypes[i] = String.class;
                    paramValues[i] = argumentValue.getValue();
                }
            }
            try {
                // 按照特定的构造器创建示例
                Constructor<?> con = clz.getConstructor(paramTypes);
                obj = con.newInstance(paramValues);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else { // 如果没有参数，直接创建实例
            try {
                obj = clz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    private void populateBean(BeanDefinition beanDefinition, Class<?> clz, Object obj) {
        handleProperties(beanDefinition, clz, obj);
    }

    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        // 处理属性
        PropertyValues propertyValues = bd.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            for (int i = 0; i < propertyValues.size(); i++) {
                //对每一个属性，分数据类型分别处理
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String type = propertyValue.getType();
                String name = propertyValue.getName();
                Object value = propertyValue.getValue();
                boolean ref = propertyValue.isRef();

                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                if (!ref) {
                    if ("String".equals(type) || "java.lang.String".equals(type)) {
                        paramTypes[0] = String.class;
                    } else if ("Integer".equals(type) || "java.lang.Integer".equals(type)) {
                        paramTypes[0] = Integer.class;
                    } else if ("int".equals(type)) {
                        paramTypes[0] = int.class;
                    } else { // 默认为string
                        paramTypes[0] = String.class;
                    }
                    paramValues[0] = value;
                } else {
                    try {
                        paramTypes[0] = Class.forName(type);
                        paramValues[0] = getBean((String) value);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                //按照setXxxx规范查找setter方法，调用setter方法设置属性
                String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method method;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                    method.invoke(obj, paramValues);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void registerBean(BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanDefinition.getId(), beanDefinition);
        this.beanDefinitionNames.add(beanDefinition.getId());
    }

    public abstract Object applyBeanPostProcessorsAfterInitialization(Object singleton, String beanName);

    public abstract Object applyBeanPostProcessorsBeforeInitialization(Object singleton, String beanName);

}
