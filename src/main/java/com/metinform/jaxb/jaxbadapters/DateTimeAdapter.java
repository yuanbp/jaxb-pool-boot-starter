package com.metinform.jaxb.jaxbadapters;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeAdapter extends XmlAdapter<String, Date> {

    private static final Logger LOG = LoggerFactory.getLogger(DateTimeAdapter.class);

    public static final String STANDARM_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static SimpleDateFormat FullFormat = new SimpleDateFormat(STANDARM_DATE_FORMAT);

    @Override
    public Date unmarshal(String v) throws Exception {
        if (StringUtils.isBlank(v)) {
            return null;
        }
        if (!isValidDateStr(v)) {
            throw new Exception("时间格式异常,时间格式必须为: " + STANDARM_DATE_FORMAT);
        }
        DateFormat format = new SimpleDateFormat(STANDARM_DATE_FORMAT);
        return format.parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        if(null == v) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(STANDARM_DATE_FORMAT);
        if (!isValidDate(v)) {
            throw new Exception("时间格式异常,时间格式必须为: " + STANDARM_DATE_FORMAT);
        }
        return format.format(v);
    }

    public static boolean isValidDateStr(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(STANDARM_DATE_FORMAT);
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static boolean isValidDate(Date date) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat(STANDARM_DATE_FORMAT);
        try {
            format.setLenient(false);
            format.format(date);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }
}