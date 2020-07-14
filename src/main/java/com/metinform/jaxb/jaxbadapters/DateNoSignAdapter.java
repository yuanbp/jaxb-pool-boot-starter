package com.metinform.jaxb.jaxbadapters;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateNoSignAdapter extends XmlAdapter<String, Date> {

    private static final Logger LOG = LoggerFactory.getLogger(DateNoSignAdapter.class);

    private static final String STANDARM_DATE_FORMAT = "yyyyMMdd";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(STANDARM_DATE_FORMAT);

    @Override
    public Date unmarshal(String v) throws Exception {
        if (StringUtils.isBlank(v)) {
            return null;
        }
        Date date = isValidDateStr(v);
        if(null == date) {
            throw new Exception("时间格式异常,时间格式必须为: " + STANDARM_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public String marshal(Date v) throws Exception {
        if(null == v) {
            return null;
        }
        String result = isValidDate(v);
        if(StringUtils.isBlank(result)) {
            throw new Exception("时间格式异常,时间格式必须为: " + STANDARM_DATE_FORMAT);
        }
        return result;
    }

    private static Date isValidDateStr(String str) {
        try {
            return Date.from(LocalDate.from(FORMATTER.parse(str)).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            LOG.error("日期转换失败", e);
            return null;
        }
    }

    private static String isValidDate(Date date) {
        try {
            return FORMATTER.format(date.toInstant());
        } catch (Exception e) {
            LOG.error("日期格式化失败", e);
            return null;
        }
    }
}