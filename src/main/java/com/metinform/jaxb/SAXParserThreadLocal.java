package com.metinform.common.jaxb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author chieftain
 * @date 2020/7/14 16:05
 */
public class SAXParserThreadLocal {

    private static final Logger log = LoggerFactory.getLogger(SAXParserThreadLocal.class);

    public static ThreadLocal<SAXParser> parser = ThreadLocal.withInitial(() -> {
        SAXParser newParser = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // 是否忽略命名空间 true 为不忽略命名空间 false 为 忽略命名空间
        factory.setNamespaceAware(true);
        try {
            newParser = factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            log.error(e.getMessage(), e);
        }
        return newParser;
    });
}
