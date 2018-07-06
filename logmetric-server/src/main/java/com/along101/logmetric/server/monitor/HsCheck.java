package com.along101.logmetric.server.monitor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yinzuolong on 2017/7/27.
 */
@RestController
public class HsCheck {

    @GetMapping("/hs")
    public String check() {
        return "OK";
    }
}
