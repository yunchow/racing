package org.racing.seda.provider;

import org.racing.seda.Decoder;

/**
 * Created by zhouyun on 2016/4/22.
 */
public class StringDecoder implements Decoder<String> {

    @Override
    public String decode(String input) {
        return input;
    }
}
