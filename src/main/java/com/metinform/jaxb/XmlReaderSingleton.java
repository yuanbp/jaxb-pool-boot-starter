package com.metinform.jaxb;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class XmlReaderSingleton {

    private static volatile XMLReader xmlReader;

    private static volatile XMLReader xmlReaderIgnoreNamespace;

    private XmlReaderSingleton() {

    }

    public static XMLReader getXMLReader() throws SAXException, ParserConfigurationException {
        if (xmlReader == null) {
            synchronized (ConstantSet.XML_READER_LOCK) {
                if (xmlReader == null) {
                    SAXParserFactory sax = SAXParserFactory.newInstance();
                    // 是否忽略命名空间 true 为不忽略命名空间 false 为 忽略命名空间
                    sax.setNamespaceAware(true);
                    xmlReader = sax.newSAXParser().getXMLReader();
                }
            }
        }
        return xmlReader;
    }

    public static XMLReader getXMLReaderNamespaceFilter() throws SAXException, ParserConfigurationException {
        if (xmlReaderIgnoreNamespace == null) {
            synchronized (ConstantSet.XML_READER_LOCK) {
                if (xmlReaderIgnoreNamespace == null) {
                    SAXParserFactory sax = SAXParserFactory.newInstance();
                    // 是否忽略命名空间 true 为不忽略命名空间 false 为 忽略命名空间
                    sax.setNamespaceAware(false);
                    xmlReaderIgnoreNamespace = sax.newSAXParser().getXMLReader();
                }
            }
        }
        return xmlReaderIgnoreNamespace;
    }
}