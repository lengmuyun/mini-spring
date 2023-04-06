package com.minis.beans.factory.xml;

import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.config.ArgumentValue;
import com.minis.beans.factory.config.ArgumentValues;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.core.Resource;
import com.minis.core.SimpleBeanFactory;
import org.dom4j.Element;

import java.util.List;

public class XmlBeanDefinitionReader {

    private final SimpleBeanFactory simpleBeanFactory;

    public XmlBeanDefinitionReader(SimpleBeanFactory beanFactory) {
        this.simpleBeanFactory = beanFactory;
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

            ArgumentValues argumentValues = getArgumentValues(element);
            beanDefinition.setConstructorArgumentValues(argumentValues);

            this.simpleBeanFactory.registerBean(beanDefinition);
        }
    }

    private ArgumentValues getArgumentValues(Element element) {
        List<Element> constructorArgElements = element.elements("constructor-arg");
        ArgumentValues argumentValues = new ArgumentValues();
        for (Element ele : constructorArgElements) {
            String type = ele.attributeValue("type");
            String name = ele.attributeValue("name");
            String value = ele.attributeValue("value");
            argumentValues.addArgumentValue(new ArgumentValue(value, type, name));
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
