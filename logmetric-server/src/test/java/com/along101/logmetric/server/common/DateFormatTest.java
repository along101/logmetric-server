package com.along101.logmetric.server.common;

import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created by yinzuolong on 2017/4/15.
 */
public class DateFormatTest {

    @Test
    public  void testDateFormat(){
        String indexDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        String s = DateFormatUtils.format(Calendar.getInstance(), indexDateFormat);
        System.out.println(s);
    }
}
