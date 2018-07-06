package com.along101.logmetric.server.worker;

import java.util.List;

/**
 * Created by yinzuolong on 2017/3/14.
 */
public interface IMessageHandler {

    boolean handle(List<MessageBox> messages);

}
