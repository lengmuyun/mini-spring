package com.minis.beans.factory.xml;

import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.annotation.AutowireCapableBeanFactory;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.List;

public class XmlBeanDefinitionReader {

    private final AutowireCapableBeanFactory beanFactory;

    public XmlBeanDefinitionReader(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);

            PropertyValues pvs = getPropertyValues(element);
            beanDefinition.setPropertyValues(pvs);

            String[] refs = pvs.getPropertyValueList().stream()
                    .filter(PropertyValue::isRef)
                    .map(i -> (String) i.getValue())
                    .toArray(String[]::new);
            beanDefinition.setDependsOn(refs);

            ConstructorArgumentValues argumentValues = getArgumentValues(element);
            beanDefinition.setConstructorArgumentValues(argumentValues);

            this.beanFactory.registerBean(beanDefinition);
        }
    }

    private ConstructorArgumentValues getArgumentValues(Element element) {
        List<Element> constructorArgElements = element.elements("constructor-arg");
        ConstructorArgumentValues argumentValues = new ConstructorArgumentValues();
        for (Element ele : constructorArgElements) {
            String type = ele.attributeValue("type");
            String name = ele.attributeValue("name");
            String value = ele.attributeValue("value");
            argumentValues.addArgumentValue(new ConstructorArgumentValue(value, type, name));
        }
        return argumentValues;
    }

    private PropertyValues getPropertyValues(Element element) {
        List<Element> propertyElements = element.elements("property");
        PropertyValues pvs = new PropertyValues();
        for (Element ele : propertyElements) {
            String type = ele.attributeValue("type");
            String name = ele.attributeValue("name");
            String value = ele.attributeValue("value");
            String ref = ele.attributeValue("ref");
            String actualValue = "";
            boolean isRef = false;
            if (value != null && !value.equals("")) {
                isRef = false;
                actualValue = value;
            } else if (ref != null && !ref.equals("")) {
                isRef = true;
                actualValue = ref;
            }
            pvs.addPropertyValue(new PropertyValue(type, name, actualValue, isRef));
        }
        return pvs;
    }

}
