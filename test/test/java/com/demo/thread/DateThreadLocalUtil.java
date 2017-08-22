package com.demo.thread;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fangcong on 2017/8/22.
 */
public class DateThreadLocalUtil {

    private static final String DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";

    /**
     * 方式1
     */
    private static ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<DateFormat>(){

        @Override
        protected synchronized DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMATE);
        }
    };

    public static DateFormat getDateFormat() {
        return dateFormatThreadLocal.get();
    }

    /**
     * 方式2
     */
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();

    public static DateFormat getDateFormat2() {
        if (threadLocal.get() == null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATE);
            threadLocal.set(sdf);
            return sdf;
        }
        return threadLocal.get();
    }

    public static Date parse(String textDate) throws Exception{
        return getDateFormat().parse(textDate);
    }

    public static String format(Date date) {
        return getDateFormat().format(date);
    }

    public static void remove() {
        dateFormatThreadLocal.remove();
    }
}
