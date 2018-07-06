package com.along101.logmetric.common.serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinzuolong on 2017/3/16.
 */
public class B {
    private int num;
    private String str;
    private List<String> list;

    public B() {
        num = 3;
        str = "rpc";

        list = new ArrayList<String>();
        list.add("rpc-list");
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("B{").append("num:").append(num).append(",str:").append(str).append(",list:").append(list).append("}");
        return str.toString();
    }
}
