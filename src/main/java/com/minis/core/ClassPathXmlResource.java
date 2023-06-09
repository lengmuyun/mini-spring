package com.minis.core;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.URL;
import java.util.Iterator;

public class ClassPathXmlResource implements Resource {

    private Document document;

    private Element rootElement;

    private Iterator<Element> elementIterator;

    public ClassPathXmlResource(String fileName) {
        URL xmlPath = this.getClass().getClassLoader().getResource(fileName);
        load(xmlPath);
    }

    public ClassPathXmlResource(URL url) {
        load(url);
    }

    private void load(URL xmlPath) {
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(xmlPath);
            rootElement = document.getRootElement();
            elementIterator = rootElement.elementIterator();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasNext() {
        return elementIterator.hasNext();
    }

    @Override
    public Object next() {
        return elementIterator.next();
    }

}
