package com.along101.logmetric.config;

import javax.xml.bind.annotation.XmlAnyElement;

import org.w3c.dom.Element;

public class BaseRootConfig {
    private Element[] others;

    @XmlAnyElement
    public Element[] getOthers() {
        return others;
    }

    public void setOthers(Element[] others) {
        this.others = others;
    }
}
