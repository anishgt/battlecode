package team031.util;

import battlecode.common.GameConstants;
import battlecode.common.MapLocation;

/**
 * Stores loc -> non negative integers
 */
public class FastLocMap {
    public static final int HASH = Math.max(GameConstants.MAP_MAX_WIDTH, GameConstants.MAP_MAX_HEIGHT);
    public static final int OFFSET = (Constants.MAX_MAP_OFFSET / HASH + 1) * HASH;

    public int[][] has = new int[HASH][HASH];

    public void add(MapLocation loc, int num) {
        has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH] = num + 1;
    }

    public int get(MapLocation loc) {
        return has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH] - 1;
    }

    public void remove(MapLocation loc) {
        has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH] = 0;
    }

    public boolean contains(MapLocation loc) {
        return has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH] > 0;
    }

    public void clear() {
        has = new int[HASH][HASH];
    }
}
