package com.along101.logmetric.server.worker;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by yinzuolong on 2017/3/24.
 */
public class HandlerWrapper implements IMessageHandler {
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private IMessageHandler handler;

    @Override
    public boolean handle(List<MessageBox> messageBox) {
        return handler.handle(messageBox);
    }
}
