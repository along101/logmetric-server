package com.along101.logmetric.common;

import com.along101.logmetric.common.bean.Message;

import java.util.List;

public interface IEncoder<E> {
    Message encode(List<E> list);
}
