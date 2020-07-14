package com.metinform.jaxb.jaxbadapters;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeNoSignAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final Logger LOG = LoggerFactory.getLogger(LocalDateTimeNoSignAdapter.class);

    private static final String STANDARM_DATE_FORMAT = "yyyyMMddHHmmss";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(STANDARM_DATE_FORMAT);

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        if (StringUtils.isBlank(v)) {
            return null;
        }
        LocalDateTime date = isValidDateStr(v);
        if(null == date) {
            throw new Exception("时间格式异常,时间格式必须为: " + STANDARM_DATE_FORMAT);
        }
        return date;
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        if(null == v) {
            return null;
        }
        String result = isValidDate(v);
        if(StringUtils.isBlank(result)) {
            throw new Exception("时间格式异常,时间格式必须为: " + STANDARM_DATE_FORMAT);
        }
        return result;
    }

    private static LocalDateTime isValidDateStr(String str) {
        try {
            return LocalDateTime.from(FORMATTER.parse(str));
        } catch (Exception e) {
            LOG.error("日期转换失败", e);
            return null;
        }
    }

    private static String isValidDate(LocalDateTime date) {
        try {
            return FORMATTER.format(date);
        } catch (Exception e) {
            LOG.error("日期格式化失败", e);
            return null;
        }
    }
}