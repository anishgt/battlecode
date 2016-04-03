package team031;

import battlecode.common.Direction;

/**
 * Operations on directions using ordinals
 */
public class DirectionOrdinals {
    public static final Direction[] directions = Direction.values();
    public static final Direction[] updateHugDirLeft = updateHugDirLeft();
    public static final Direction[] updateHugDirRight = updateHugDirRight();

    private static int[] rotateLeft() {
        Direction[] values = Direction.values();
        int length = values.length;
        int[] result = new int[length];
        for (int i = length - 1; i >= 0; i--) {
            Direction dir = values[i];
            result[dir.ordinal()] = dir.rotateLeft().ordinal();
        }
        return result;
    }

    private static int[] rotateRight() {
        Direction[] values = Direction.values();
        int length = values.length;
        int[] result = new int[length];
        for (int i = length - 1; i >= 0; i--) {
            Direction dir = values[i];
            result[dir.ordinal()] = dir.rotateRight().ordinal();
        }
        return result;
    }

    private static Direction[] updateHugDirLeft() {
        Direction[] values = Direction.values();
        int length = values.length;
        Direction[] result = new Direction[length];
        for (int i = length - 1; i >= 0; i--) {
            Direction dir = values[i];
            if (dir.isDiagonal()) {
                result[dir.ordinal()] = dir.rotateRight().rotateRight().rotateRight();
            } else {
                result[dir.ordinal()] = dir.rotateRight().rotateRight();
            }
        }
        return result;
    }

    private static Direction[] updateHugDirRight() {
        Direction[] values = Direction.values();
        int length = values.length;
        Direction[] result = new Direction[length];
        for (int i = length - 1; i >= 0; i--) {
            Direction dir = values[i];
            if (dir.isDiagonal()) {
                result[dir.ordinal()] = dir.rotateLeft().rotateLeft().rotateLeft();
            } else {
                result[dir.ordinal()] = dir.rotateLeft().rotateLeft();
            }
        }
        return result;
    }
}
