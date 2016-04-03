package team031.update;

import battlecode.common.RobotType;

/**
 * Shared variables for archon
 */
public class Archon {
    public static int[] spawned;
    public static void init() {
        spawned = new int[RobotType.values().length];
    }
}
