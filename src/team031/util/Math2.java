package team031.util;

/**
 * Created by jdshen on 1/5/16.
 */
public class Math2 {
    public static int max(int x, int y) {
        return x > y ? x : y;
    }

    public static int min(int x, int y) {
        return x < y ? x : y;
    }

    public static int floorDivision(int n, int d) {
        return n >= 0 ? n / d : ~(~n / d);
    }
}
