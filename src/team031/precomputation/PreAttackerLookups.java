package team031.precomputation;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import team031.util.Constants;

import java.util.ArrayList;

/**
 * Created by jdshen on 1/6/16.
 */
public class PreAttackerLookups {
    public static void main(String[] args) {
        int[] ranges = new int[]{2, 13, 20, 24, 40, Constants.AUGMENTED_TURRET_RANGE};
        long[][][] table = calcRangeTable(ranges, 10, 10, 21, 21);// x, 2x + 1
        printSwitchCase(table, ranges, 10, 10);
    }

    public static long dirToLong(Direction dir) {
        if (dir == null) {
            return 0L;
        }
        switch (dir) {
            case NORTH:
                return 0x000000001L;
            case NORTH_EAST:
                return 0x000000010L;
            case EAST:
                return 0x000000100L;
            case SOUTH_EAST:
                return 0x000001000L;
            case SOUTH:
                return 0x000010000L;
            case SOUTH_WEST:
                return 0x000100000L;
            case WEST:
                return 0x001000000L;
            case NORTH_WEST:
                return 0x010000000L;
            case OMNI:
                return 0x100000000L;
            case NONE:
                return 0x000000000L;
        }
        return 0x000000000L;
    }

    public static long pack(ArrayList<Direction> dirs) {
        long k = 0;
        for (Direction dir : dirs) {
            k += dirToLong(dir);
        }

        return k;
    }

    public static long[][][] calcRangeTable(int[] ranges, int cx, int cy, int maxX, int maxY) {
        MapLocation center = new MapLocation(0, 0);
        long[][][] results = new long[ranges.length][maxX][maxY];
        for (int i = 0; i < ranges.length; i++) {
            int range = ranges[i];
            for (int j = 0; j < maxX; j++) {
                for (int k = 0; k < maxY; k++) {
                    MapLocation loc = new MapLocation(j - cx, k - cy);
                    ArrayList<Direction> dirs = new ArrayList<>();
                    for (Direction d : Direction.values()) {
                        if (loc.distanceSquaredTo(center.add(d)) <= range) {
                            dirs.add(d);
                        }
                    }
                    results[i][j][k] = pack(dirs);
                }
            }
        }
        return results;
    }

    public static String toJavaLong(long l) {
        return "0x" + Long.toHexString(l) + "L";
    }

    public static void printSwitchCase(long[][][] results, int[] ranges, int cx, int cy) {
        System.out.println("switch (range) {");
        for (int i = 0; i < results.length; i++) {
            System.out.println("case " +  ranges[i] + ":");
            System.out.println("switch(x) {");
            for (int j = 0; j < results[i].length; j++) {
                System.out.println("case " + (j - cx) + ":");
                System.out.println("switch(y) {");
                for (int k = 0; k < results[i][j].length; k++) {
                    System.out.println("case " + (k - cy) + ":");
                    System.out.println("return " + toJavaLong(results[i][j][k])+ ";");
                }
                System.out.println("default:");
                System.out.println("return 0;");
                System.out.println("}");
            }
            System.out.println("default:");
            System.out.println("return 0;");
            System.out.println("}");
        }
        System.out.println("}");
        System.out.println("return 0;");
    }
}
