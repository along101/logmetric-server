package com.along101.logmetric.common.serialization;

import com.along101.logmetric.common.util.KryoUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yinzuolong on 2017/3/16.
 */
public class KryoUtilTest {
    @Test
    public void test() {
        B b = new B();
        A a = new A();
        a.setB(b);

        B bb = (B) a.getB();
        System.out.println("print  A-B-List before  serialize");
        System.out.println(a);
        byte[] test = KryoUtil.serialize(a);
        A newA = KryoUtil.deserialize(test, A.class);
        System.out.println("print   A-B-List after serialize");
        System.out.println(newA);

        Assert.assertEquals(a.toString(), newA.toString());
    }
}
