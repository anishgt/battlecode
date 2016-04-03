package team031.messaging;

import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team031.util.Constants;
import team031.util.Math2;

/**
 * Created by jdshen on 1/5/16.
 */
public class Messager {
    private static final int MAX_TYPES = 32;
    private static final int HALF_LOC_MOD = Math2.max(GameConstants.MAP_MAX_HEIGHT, GameConstants.MAP_MAX_WIDTH);
    private static final int LOC_MOD = 2 * HALF_LOC_MOD;
    public static final int ID_MOD = 32768; // add some buffer to 32000
    private static final int MAX_LOCS = 4;
    private static final int MAX_GROUPS = 4;
    private static final int MAX_MAP_OFFSET = (Constants.MAX_MAP_OFFSET / LOC_MOD + 1) * LOC_MOD;

    public static int X;
    public static int Y;

    public static void init(RobotController rc) {
        MapLocation loc = rc.getLocation();
        X = loc.x;
        Y = loc.y;
    }

    public static int hash(int id, int round) {
        return (id * 31 + round) % ID_MOD;
    }

    // pack in reverse order
    public static int[] encode(Message m) {
        int OFFSET = MAX_MAP_OFFSET;
        int LOC_MOD = Messager.LOC_MOD;
        MessageType t = m.type;
        MapLocation[] locs = m.locs;
        int num = 0;
        if (locs != null) {
            num = locs.length;
        }

        int x;
        int y;
        switch (t.getType()) {
            // only type

            // only id
            case ID:
                return new int[]{t.ordinal(), m.id};

            // only map locations
            case LOCS:
                y = ((locs[0].y + OFFSET) % LOC_MOD) * LOC_MOD + (locs[0].x + OFFSET) % LOC_MOD;

                if (num == 1) {
                    return new int[]{MAX_TYPES + t.ordinal(), y};
                }

                x = ((locs[1].y + OFFSET) % LOC_MOD) * LOC_MOD + (locs[1].x + OFFSET) % LOC_MOD;

                if (num == 2) {
                    return new int[]{(x * MAX_LOCS + num) * MAX_TYPES + t.ordinal(), y};
                }

                y = (y * LOC_MOD + (locs[2].y + OFFSET) % LOC_MOD) * LOC_MOD
                        + (locs[2].x + OFFSET) % LOC_MOD;

                return new int[]{(x * MAX_LOCS + num) * MAX_TYPES + t.ordinal(), y};

            // both
            case ID_AND_LOCS:
                y = ((locs[0].y + OFFSET) % LOC_MOD) * LOC_MOD + (locs[0].x + OFFSET) % LOC_MOD;

                if (num == 1) {
                    return new int[]{MAX_TYPES + t.ordinal(), y * ID_MOD + m.id};
                }

                x = ((locs[1].y + OFFSET) % LOC_MOD) * LOC_MOD + (locs[1].x + OFFSET) % LOC_MOD;

                return new int[]{(x * MAX_LOCS + num) * MAX_TYPES + t.ordinal(), y * ID_MOD + m.id};
            case ID_GROUP_LOCS:
                y = ((locs[0].y + OFFSET) % LOC_MOD) * LOC_MOD + (locs[0].x + OFFSET) % LOC_MOD;

                if (num == 1) {
                    return new int[]{MAX_TYPES + t.ordinal(), y * ID_MOD + m.id};
                }

                x = ((locs[1].y + OFFSET) % LOC_MOD) * LOC_MOD + (locs[1].x + OFFSET) % LOC_MOD;

                return new int[]{((x * MAX_LOCS + num) * MAX_GROUPS + m.group) * MAX_TYPES + t.ordinal(), y * ID_MOD + m.id};
        }
        return new int[]{0, 0};
    }

    public static MessageType type(int[] x) {
        return MessageType.values()[x[0] % MAX_TYPES];
    }

    public static int id(int[] x) {
        return x[1] % ID_MOD;
    }

    // unpack in forward order
    public static Message decode(int x, int y) {
        Message m = new Message();
        MapLocation[] locs;
        int length;
        int LOC_MOD = Messager.LOC_MOD;
        m.type = MessageType.values()[x % MAX_TYPES];
        switch (m.type.getType()) {
            // only type

            // only id
            case ID:
                m.id = y;
                return m;

            // only map locations
            case LOCS:
                x /= MAX_TYPES;
                length = x % MAX_LOCS;
                locs = new MapLocation[length];
                x /= MAX_LOCS;

                if (length >= 3) {
                    int a = y % LOC_MOD;
                    y /= LOC_MOD;
                    int b = y % LOC_MOD;
                    y /= LOC_MOD;
                    locs[2] = get(a, b);
                }

                if (length >= 2) {
                    locs[1] = get(x % LOC_MOD, (x / LOC_MOD) % LOC_MOD);
                }

                if (length >= 1) {
                    locs[0] = get(y % LOC_MOD, (y / LOC_MOD) % LOC_MOD);
                }

                m.locs = locs;
                return m;
            case ID_AND_LOCS:
                x /= MAX_TYPES;
                length = x % MAX_LOCS;
                locs = new MapLocation[length];
                x /= MAX_LOCS;
                m.id = y % ID_MOD;
                y /= ID_MOD;

                if (length >= 2) {
                    locs[1] = get(x % LOC_MOD, (x / LOC_MOD) % LOC_MOD);
                }

                if (length >= 1) {
                    locs[0] = get(y % LOC_MOD, (y / LOC_MOD) % LOC_MOD);
                }

                m.locs = locs;
                return m;
            case ID_GROUP_LOCS:
                x /= MAX_TYPES;
                m.group = x % MAX_GROUPS;
                x /= MAX_GROUPS;
                length = x % MAX_LOCS;
                locs = new MapLocation[length];
                x /= MAX_LOCS;
                m.id = y % ID_MOD;
                y /= ID_MOD;

                if (length >= 2) {
                    locs[1] = get(x % LOC_MOD, (x / LOC_MOD) % LOC_MOD);
                }

                if (length >= 1) {
                    locs[0] = get(y % LOC_MOD, (y / LOC_MOD) % LOC_MOD);
                }

                m.locs = locs;
                return m;
        }
        return m;
    }

    public static MapLocation get(int a, int b) {
        int MAX_MAP_OFFSET = Messager.MAX_MAP_OFFSET;
        int LOC_MOD = Messager.LOC_MOD;
        a = (a + MAX_MAP_OFFSET - X) % LOC_MOD;
        if (a >= HALF_LOC_MOD) {
            a -= LOC_MOD;
        }
        b = (b + MAX_MAP_OFFSET - Y) % LOC_MOD;
        if (b >= HALF_LOC_MOD) {
            b -= LOC_MOD;
        }

        return new MapLocation(a + X, b + Y);
    }


    // for reference only, should be inlined
    public static int pack(MapLocation loc) {
        int MAX_MAP_OFFSET = Messager.MAX_MAP_OFFSET;
        int x = 0;
        x *= LOC_MOD;
        x += (loc.y + MAX_MAP_OFFSET) % LOC_MOD;
        x *= LOC_MOD;
        x += (loc.x + MAX_MAP_OFFSET) % LOC_MOD;
        return x;
    }
}
