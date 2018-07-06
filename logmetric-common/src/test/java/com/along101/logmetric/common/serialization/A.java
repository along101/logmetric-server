package com.along101.logmetric.common.serialization;

/**
 * Created by yinzuolong on 2017/3/16.
 */
public class A {
    Object b;

    public Object getB() {
        return b;
    }

    public void setB(Object b) {
        this.b = b;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("A{").append(b.toString()).append("}");
        return str.toString();
    }
}