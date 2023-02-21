package com.juanmuscaria.blockproxy.jna.types;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

public class Size_t extends IntegerType {
    public static int SIZE;

    public Size_t() {
        this(0L);
    }

    public Size_t(long value) {
        super(SIZE, value);
    }

    static {
        SIZE = Native.SIZE_T_SIZE;
    }
}
