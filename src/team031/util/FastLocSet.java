package team031.util;

import battlecode.common.GameConstants;
import battlecode.common.MapLocation;

public class FastLocSet implements LocSet{
    public static final int HASH = Math.max(GameConstants.MAP_MAX_WIDTH, GameConstants.MAP_MAX_HEIGHT);
    public static final int OFFSET = (Constants.MAX_MAP_OFFSET / HASH + 1) * HASH;

    public boolean[][] has = new boolean[HASH][HASH];

    public void add(MapLocation loc) {
        has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH] = true;
    }

    public void remove(MapLocation loc) {
        has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH] = false;
    }

    public boolean contains(MapLocation loc) {
        return has[(loc.x + OFFSET) % HASH][(loc.y + OFFSET) % HASH];
    }

    public void clear() {
        has = new boolean[HASH][HASH];
    }
}