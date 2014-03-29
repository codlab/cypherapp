package eu.codlab.cyphersend.utils;

/**
 * Created by kevinleperf on 28/06/13.
 */
public class Color {
    private final static int[] _colors = new int[]{
            0xff33eab6,
            0xff33b6ea,
            0xffb633ea,
            0xffb3ea63,
            0xffb3e6a3,
    };
    public static int getColor(int index){
        return _colors[ index % _colors.length];
    }
}
