package team031.precomputation;

import battlecode.common.Direction;

/**
 * Created by jdshen on 1/7/16.
 */
public class ClosestDirectionLookups {

    public static void main(String[] args) {
        printSwitchCase(calcTable());
    }

    public static Direction[][] calcTable() {
        Direction[][] dir = new Direction[Direction.values().length][];
        for (int i = 0; i < dir.length; i++) {
            Direction d = Direction.values()[i];
            if (d == Direction.NONE) {
                dir[i] = new Direction[0];
            } else if (d.isDiagonal()) {
                dir[i] = new Direction[] {
                    d, d.rotateLeft(), d.rotateRight(), Direction.OMNI,
                    d.rotateLeft().rotateLeft(), d.rotateRight().rotateRight(),
                    d.rotateLeft().rotateLeft().rotateLeft(), d.rotateRight().rotateRight().rotateRight(),
                    d.opposite()
                };
            } else if (d == Direction.OMNI){
                dir[i] = new Direction[] {
                    Direction.OMNI,
                    Direction.NORTH,
                    Direction.NORTH_EAST,
                    Direction.EAST,
                    Direction.SOUTH_EAST,
                    Direction.SOUTH,
                    Direction.SOUTH_WEST,
                    Direction.WEST,
                    Direction.NORTH_WEST,
                };
            } else{
                dir[i] = new Direction[] {
                    d, d.rotateLeft(), d.rotateRight(), Direction.OMNI,
                    d.rotateLeft().rotateLeft(), d.rotateRight().rotateRight(),
                    d.opposite(),
                    d.rotateLeft().rotateLeft().rotateLeft(), d.rotateRight().rotateRight().rotateRight(),
                };
            }
        }
        return dir;
    }

    // NOTE - we look through j in reverse order since we want to iterate in reverse order usually
    public static void printSwitchCase(Direction[][] results) {
        System.out.println("new Direction[][] {");
        for (int i = 0; i < results.length; i++) {
            System.out.println("new Direction[] {");
            for (int j = results[i].length - 1; j >= 0; j--) {
                System.out.println("Direction." + results[i][j].name() + ",");
            }
            System.out.println("},");
        }
        System.out.println("};");
    }
}
