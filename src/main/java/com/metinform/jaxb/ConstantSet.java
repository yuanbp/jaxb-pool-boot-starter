package com.metinform.jaxb;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * com.xdatacloud.core.util [workset]
 * Created by chieftain on 2018/11/26
 *
 * @author chieftain on 2018/11/26
 */
public class ConstantSet implements Serializable {

    public static final Object MARSHALLER_LOCK = new Object();

    public static final Object UNMARSHALLER_LOCK = new Object();

    public static final Object XML_READER_LOCK = new Object();

    public static final ReentrantLock MARSHALLER_REENTRANT_LOCK = new ReentrantLock();

    public static final ReentrantLock UNMARSHALLER_REENTRANT_LOCK = new ReentrantLock();
}
