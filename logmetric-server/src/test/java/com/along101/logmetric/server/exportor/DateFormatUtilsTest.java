package com.along101.logmetric.server.exportor;

import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

import java.util.Calendar;

/**
 * Created by yinzuolong on 2017/4/11.
 */
public class DateFormatUtilsTest {

    @Test
    public void testFormat(){
        String d = DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(d);
//        new RuntimeException("").getStackTrace()
    }
}
