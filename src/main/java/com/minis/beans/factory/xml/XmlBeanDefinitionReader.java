package com.minis.beans.factory.xml;

import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.config.ArgumentValue;
import com.minis.beans.factory.config.ArgumentValues;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.core.Resource;
import com.minis.core.SimpleBeanFactory;
import org.dom4j.Element;

import java.util.ArrayList;
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

            List<Element> propertyElements = element.elements("property");
            List<String> refs = new ArrayList<>();
            PropertyValues pvs = new PropertyValues();
            for (Element propElement : propertyElements) {
                String pType = propElement.attributeValue("type");
                String pName = propElement.attributeValue("name");
                String pValue = propElement.attributeValue("value");
                String pRef = propElement.attributeValue("ref");
                String pV = "";
                boolean isRef = false;
                if (pValue != null && !pValue.equals("")) {
                    isRef = false;
                    pV = pValue;
                } else if (pRef != null && !pRef.equals("")) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                pvs.addPropertyValue(new PropertyValue(pType, pName, pValue, isRef));
            }
            beanDefinition.setPropertyValues(pvs);
            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);

            List<Element> constructorArgElements = element.elements("constructor-arg");
            ArgumentValues argumentValues = new ArgumentValues();
            for (Element argElement : constructorArgElements) {
                String pType = argElement.attributeValue("type");
                String pName = argElement.attributeValue("name");
                String pValue = argElement.attributeValue("value");
                argumentValues.addArgumentValue(new ArgumentValue(pValue, pType, pName));
            }
            beanDefinition.setConstructorArgumentValues(argumentValues);

            this.simpleBeanFactory.registerBean(beanDefinition);
        }
    }

}
