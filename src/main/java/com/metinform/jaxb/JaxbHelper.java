package com.metinform.jaxb;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * com.xdatacloud.core.util [workset]
 * Created by chieftain on 2018/11/29
 *
 * @author chieftain on 2018/11/29
 */
public class JaxbHelper {

    private boolean appendTag, formatContent;

    public JaxbHelper(boolean appendTag, boolean formatContent) {
        this.appendTag = appendTag;
        this.formatContent = formatContent;
    }

    private final String tag = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";

    /**
     * 对象转xml常用
     *
     * @param rootObject
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> String toXmlUsePool(T rootObject) throws Exception {
        Marshaller marshaller = null;
        StringWriter writer = null;
        try {
            marshaller = MarshallerKeyPoolFactory.borrowBean(rootObject.getClass().getName());
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatContent);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setListener(new MarshallerListener());
            writer = new StringWriter();
            if (appendTag) {
                writer.append(tag);
            }
            synchronized (ConstantSet.MARSHALLER_LOCK) {
                marshaller.marshal(rootObject, writer);
            }
            return writer.toString();
        } finally {
            if (null != marshaller) {
                MarshallerKeyPoolFactory.returnBean(rootObject.getClass().getName(), marshaller);
            }
            if (null != writer) {
                writer.close();
            }
        }
    }

    /**
     * xml 转对象常用
     * @param cls
     * @param xml
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T toPojoUsePool(Class<T> cls, String xml) throws Exception {
        Unmarshaller unmarshaller = null;
        InputStream inputStream = null;
        try {
            unmarshaller = UnmarshallerKeyPoolFactory.borrowBean(cls.getName());
            JAXBElement<T> element = null;
            byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
            inputStream = new UnicodeInputStream(new ByteArrayInputStream(bytes), "UTF-8");
            XMLReader xmlReader = XmlReaderSingleton.getXMLReader();
            Source source = new SAXSource(xmlReader, new InputSource(inputStream));
            synchronized (ConstantSet.UNMARSHALLER_LOCK) {
                element = unmarshaller.unmarshal(source, cls);
            }
            return element.getValue();
        } finally {
            if (null != unmarshaller) {
                UnmarshallerKeyPoolFactory.returnBean(cls.getName(), unmarshaller);
            }
            if (null != inputStream) {
                inputStream.close();
            }
        }
    }

    /**
     * 不用池
     *
     * @param rootObject
     * @param <T>
     * @return
     * @throws JAXBException
     * @throws IOException
     */
    public <T> String toXml(T rootObject) throws JAXBException, IOException {
        StringWriter writer = null;
        try {
            JAXBContext context = JAXBContext.newInstance(rootObject.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatContent);
            // 去掉默认的<?xml version="1.0" encoding="utf-8" standalone="yes"?> 通过 StringWriter 追加 <?xml version="1.0" encoding="UTF-8"?>
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setListener(new MarshallerListener());
            writer = new StringWriter();
            if (appendTag) {
                writer.append(tag);
            }
            synchronized (ConstantSet.MARSHALLER_LOCK) {
                marshaller.marshal(rootObject, writer);
            }
            return writer.toString();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

    public <T> String toXmlUsePool2(T rootObject) throws Exception {
        Marshaller marshaller = null;
        StringWriter writer = null;
        try {
            marshaller = MarshallerKeyPoolFactory.borrowBean(rootObject.getClass().getName());
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setListener(new MarshallerListener());
            writer = new StringWriter();
            writer.append(tag);
            try {
                ConstantSet.MARSHALLER_REENTRANT_LOCK.lock();
                marshaller.marshal(rootObject, writer);
            } finally {
                ConstantSet.MARSHALLER_REENTRANT_LOCK.unlock();
            }
            return writer.toString();
        } finally {
            if (null != marshaller) {
                MarshallerKeyPoolFactory.returnBean(rootObject.getClass().getName(), marshaller);
            }
            if (null != writer) {
                writer.close();
            }
        }
    }

    public <T> String toXmlDefault(T rootObject) throws JAXBException, IOException {
        StringWriter writer = null;
        try {
            JAXBContext context = JAXBContext.newInstance(rootObject.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
            marshaller.setListener(new MarshallerListener());
            writer = new StringWriter();
            writer.append(tag);
            synchronized (ConstantSet.MARSHALLER_LOCK) {
                marshaller.marshal(rootObject, writer);
            }
            return writer.toString();
        } finally {
            assert writer != null;
            writer.close();
        }
    }

    public <T> T toPojo(Class<T> cls, String xml) throws Exception {
        InputStream inputStream = null;
        try {
            JAXBElement<T> element = null;
            byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
            inputStream = new UnicodeInputStream(new ByteArrayInputStream(bytes), "UTF-8");
            XMLReader xmlReader = XmlReaderSingleton.getXMLReader();
            Source source = new SAXSource(xmlReader, new InputSource(inputStream));
            JAXBContext context = JAXBContext.newInstance(cls);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            synchronized (ConstantSet.UNMARSHALLER_LOCK) {
                element = unmarshaller.unmarshal(source, cls);
            }
            return element.getValue();
        } finally {
            assert inputStream != null;
            inputStream.close();
        }
    }

    public <T> T toPojoUsePool2(Class<T> cls, String xml) throws Exception {
        Unmarshaller unmarshaller = null;
        InputStream inputStream = null;
        try {
            unmarshaller = UnmarshallerKeyPoolFactory.borrowBean(cls.getName());
            JAXBElement<T> element = null;
            byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
            inputStream = new UnicodeInputStream(new ByteArrayInputStream(bytes), "UTF-8");
            XMLReader xmlReader = XmlReaderSingleton.getXMLReader();
            Source source = new SAXSource(xmlReader, new InputSource(inputStream));
            try {
                ConstantSet.UNMARSHALLER_REENTRANT_LOCK.lock();
                element = unmarshaller.unmarshal(source, cls);
            } finally {
                ConstantSet.UNMARSHALLER_REENTRANT_LOCK.unlock();
            }
            return element.getValue();
        } finally {
            if (null != unmarshaller) {
                UnmarshallerKeyPoolFactory.returnBean(cls.getName(), unmarshaller);
            }
            if (null != inputStream) {
                inputStream.close();
            }
        }
    }

    public <T> T toPojoNamespaceFilter(Class<T> cls, String xml) throws Exception {
        InputStream inputStream = null;
        try {
            JAXBElement<T> element = null;
            byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
            inputStream = new UnicodeInputStream(new ByteArrayInputStream(bytes), "UTF-8");
            XMLReader xmlReader = XmlReaderSingleton.getXMLReaderNamespaceFilter();
            Source source = new SAXSource(xmlReader, new InputSource(inputStream));
            JAXBContext ctx = JAXBContext.newInstance(cls);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            synchronized (ConstantSet.UNMARSHALLER_LOCK) {
                element = unmarshaller.unmarshal(source, cls);
            }
            return element.getValue();
        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
        }
    }

    public static String formatXML(String inputXML) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(inputXML));
        String requestXML = null;
        XMLWriter writer = null;
        if (document != null) {
            try {
                StringWriter stringWriter = new StringWriter();
                OutputFormat format = new OutputFormat(" ", true);
                writer = new XMLWriter(stringWriter, format);
                writer.write(document);
                writer.flush();
                requestXML = stringWriter.getBuffer().toString();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
        return requestXML;
    }
}
