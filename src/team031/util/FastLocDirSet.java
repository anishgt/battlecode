package team031.util;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;

public class FastLocDirSet {
    private static final int HASH = Math.max(GameConstants.MAP_MAX_WIDTH, GameConstants.MAP_MAX_HEIGHT);
    private static final int OFFSET = (Constants.MAX_MAP_OFFSET / HASH + 1) * HASH;

    private boolean[][][] has = new boolean[HASH][HASH][10];

    public void add(MapLocation loc, Direction dir) {
        has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH][dir.ordinal()] = true;
    }

    public void remove(MapLocation loc, Direction dir) {
        has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH][dir.ordinal()] = false;
    }

    public boolean contains(MapLocation loc, Direction dir) {
        return has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH][dir.ordinal()];
    }

    public void clear() {
        has = new boolean[HASH][HASH][10];
    }
}