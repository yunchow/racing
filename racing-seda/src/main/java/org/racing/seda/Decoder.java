package org.racing.seda;

/**
 * Created by zhouyun on 2016/4/22.
 */
public interface Decoder<T> {

    T decode(String input);
}
