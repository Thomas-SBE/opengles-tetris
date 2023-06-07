package net.tsbe.tetrominoes.utils;

import java.util.Arrays;

public class ArrayUtils {

    public static float[] concat(float[] a, float[] b){
        float[] r = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }
}
