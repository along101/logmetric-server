package com.along101.logmetric.common.bean;

import com.along101.logmetric.common.util.IPUtil;

import java.util.HashMap;


public class Header extends HashMap<String, Object> {

    public Header() {
        if (IPUtil.getLocalIP() != null && !"".equalsIgnoreCase(IPUtil.getLocalIP()))
            this.put("HOST_IP", IPUtil.getLocalIP());
    }
}
