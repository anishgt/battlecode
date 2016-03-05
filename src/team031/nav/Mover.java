package team031.nav;

import battlecode.common.*;
import team031.controllers.Controller;
import team031.lookups.RubbleClearing;
import team031.util.Constants;
import team031.util.Math2;

/**
 * Helper class for moving. Contains no state (except RobotController/Controller)
 * @author Jeffrey
 *
 */
public class Mover {
    public static boolean[] calcCanMove() {
        // localize
        Direction[] values = Direction.values();
        int length = values.length;
        boolean[] canMove = new boolean[length];
        RobotController rc = Controller.crc;
        for (int i = length - 1; i >= 0; i--) {
            canMove[i] = rc.canMove(values[i]);
        }

        canMove[Direction.OMNI.ordinal()] = true;
        return canMove;
    }

    public static MapLocation getClosest(MapLocation[] locs) {
        int min = Constants.MAX_DISTANCE_SQ;
        MapLocation cur = null;
        MapLocation here = Controller.crc.getLocation();
        for (int i = locs.length - 1; i >= 0; i--) {
            MapLocation loc = locs[i];
            int dist = here.distanceSquaredTo(loc);
            if (dist < min) {
                min = dist;
                cur = loc;
            }
        }

        return cur;
    }

    public static Direction adjacentClear(Direction dir, boolean[] canMove) throws GameActionException {
        if (dir == Direction.OMNI || dir == Direction.NONE) {
            return dir;
        }

        RobotController rc = Controller.crc;
        Controller c = Controller.c;
        Direction[] dirs = DIRS_IN_ORDER[dir.ordinal()];
        MapLocation here = rc.getLocation();
        double thresh = GameConstants.RUBBLE_SLOW_THRESH;
        Direction desired = null;
        int min = 1000; // rubble no more than 1mil;
        // consider only 3 directions
        for (int i = dirs.length - 1; i >= Math2.max(0, dirs.length - 1); i--) {
            Direction d = dirs[i];
            MapLocation loc = here.add(d);
            double rubble = rc.senseRubble(loc);
            if (!rc.onTheMap(loc)) {
                continue;
            }

            if (rubble < thresh) {
                if (canMove[d.ordinal()] && d != Direction.OMNI){
                    rc.move(d);
                    return d;
                }
            } else {
                int turns = RubbleClearing.getClears((int) rubble);
                if (turns < min) {
                    min = turns;
                    desired = d;
                }
            }
        }

        if (desired != null && desired != Direction.OMNI) {
            rc.clearRubble(desired);
        }

        return desired;
    }

    public static RobotInfo getBestTurret(RobotInfo[] allies, MapLocation here) {
        RobotController rc = Controller.crc;
        Team team = rc.getTeam();
        for (int i = Math2.min(allies.length, Constants.MAX_ALLIES) - 1; i >= 0; i--) {
            RobotInfo info = allies[i];
            if (info.type != RobotType.TURRET && info.type != RobotType.TTM) {
                continue;
            }

            MapLocation loc = info.location;
            int dist = here.distanceSquaredTo(loc);

            RobotInfo[] close = rc.senseNearbyRobots(info.location, Constants.SEMI_ADJACENT_SQ, team);
            for (int j = Math2.min(close.length, Constants.MAX_ALLIES) - 1; j >= 0; j--) {
                if (close[j].type == RobotType.SCOUT && close[j].location.distanceSquaredTo(loc) <= dist) {
                    return null;
                }
            }

            return info;
        }

        return null;
    }

    // try to move in a direction taking at most turns to clear rubble. clear until 50 or less
    public static Direction moveOrClear(Direction dir, boolean[] canMove) throws GameActionException {
        if (dir == Direction.OMNI || dir == Direction.NONE) {
            return dir;
        }

        RobotController rc = Controller.crc;
        Controller c = Controller.c;
        Direction[] dirs = DIRS_IN_ORDER[dir.ordinal()];
        MapLocation here = rc.getLocation();
        double thresh = GameConstants.RUBBLE_SLOW_THRESH;
        Direction desired = null;
        int min = 1000; // rubble no more than 1mil;
        // consider only 3 directions
        for (int i = dirs.length - 1; i >= Math2.max(0, dirs.length - 3); i--) {
            Direction d = dirs[i];
            MapLocation loc = here.add(d);
            double rubble = rc.senseRubble(loc);
            if (!rc.onTheMap(loc)) {
                continue;
            }

            if (rubble < thresh) {
                if (canMove[d.ordinal()] && d != Direction.OMNI){
                    rc.move(d);
                    return d;
                }
            } else {
                int turns = RubbleClearing.getClears((int) rubble);
                if (turns < min) {
                    min = turns;
                    desired = d;
                }
            }
        }

        if (desired != null && desired != Direction.OMNI) {
            rc.clearRubble(desired);
        }

        return desired;
    }

    public static MapLocation getArchon() {
        RobotController rc = Controller.crc;
        Controller c = Controller.c;
        RobotInfo[] infos = rc.senseNearbyRobots(c.srs, c.team);
        for (int i = Math2.min(infos.length, Constants.MAX_ALLIES)- 1; i >= 0; i--) {
            if (infos[i].type == RobotType.ARCHON) {
                return infos[i].location;
            }
        }

        return rc.getLocation();
    }

    public static boolean[] canAvoidMove(boolean[] canMove, int[] attacking, int[] turrets) {
        boolean[] avoidMove = new boolean[canMove.length];
        for (int i = canMove.length - 1; i >= 0; i--) {
            avoidMove[i] = canMove[i] && attacking[i] <= 0 && turrets[i] <= 0;
        }

        return avoidMove;
    }

    public static boolean[] canAvoidMove(boolean[] canMove, int[] attacking) {
        boolean[] avoidMove = new boolean[canMove.length];
        for (int i = canMove.length - 1; i >= 0; i--) {
            avoidMove[i] = canMove[i] && attacking[i] <= 0;
        }

        return avoidMove;
    }

    // minimize turrets, maximize attackable, and minimizing attacking
    public static Direction stayInDir(
        boolean[] canMove, int[] attacking, int[] attackable, int[] turrets, Direction dir
    ) {
        int min = 0x1110;
        Direction[] dirs = DIRS_IN_ORDER[dir.ordinal()];
        int minD = -1;

        for (int i = dirs.length - 1; i >= 0; i--) {
            int d = dirs[i].ordinal();
            if (!canMove[d]) {
                continue;
            }

            int score = turrets[d] * 0x100 + (15 - attackable[d]) * 0x10 + attacking[d];
            if (score < min) {
                min = score;
                minD = d;
            }
        }

        if (minD >= 0) {
            return Direction.values()[minD];
        }

        return null;
    }

    // minimize turrets and minimizing attacking
    public static Direction avoidInDir(boolean[] canMove, int[] attacking, int[] turrets, Direction dir) {
        int min = 0x110;
        Direction[] dirs = DIRS_IN_ORDER[dir.ordinal()];
        int minD = -1;

        for (int i = dirs.length - 1; i >= 0; i--) {
            int d = dirs[i].ordinal();
            if (!canMove[d]) {
                continue;
            }

            int score = turrets[d] * 0x10 + attacking[d];
            if (score < min) {
                min = score;
                minD = d;
            }
        }

        if (minD >= 0) {
            return Direction.values()[minD];
        }

        return null;
    }

    // chooses direction with lowest score that can be moved in, and is closest to the desired direction.
    public static Direction avoidInDir(boolean[] canMove, int[] lookups, Direction dir) {
        int min = 16;
        Direction[] dirs = DIRS_IN_ORDER[dir.ordinal()];
        int minD = -1;

        for (int i = dirs.length - 1; i >= 0; i--) {
            int d = dirs[i].ordinal();
            if (!canMove[d]) {
                continue;
            }

            if (lookups[d] < min) {
                min = lookups[d];
                minD = d;
            }
        }

        if (minD >= 0) {
            return Direction.values()[minD];
        }

        return null;
    }

    // closest dirs in REVERSE order
    public static final Direction[][] DIRS_IN_ORDER = new Direction[][] {
        new Direction[] {
            Direction.SOUTH_EAST,
            Direction.SOUTH_WEST,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST,
            Direction.OMNI,
            Direction.NORTH_EAST,
            Direction.NORTH_WEST,
            Direction.NORTH,
        },
        new Direction[] {
            Direction.SOUTH_WEST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.SOUTH_EAST,
            Direction.NORTH_WEST,
            Direction.OMNI,
            Direction.EAST,
            Direction.NORTH,
            Direction.NORTH_EAST,
        },
        new Direction[] {
            Direction.SOUTH_WEST,
            Direction.NORTH_WEST,
            Direction.WEST,
            Direction.SOUTH,
            Direction.NORTH,
            Direction.OMNI,
            Direction.SOUTH_EAST,
            Direction.NORTH_EAST,
            Direction.EAST,
        },
        new Direction[] {
            Direction.NORTH_WEST,
            Direction.WEST,
            Direction.NORTH,
            Direction.SOUTH_WEST,
            Direction.NORTH_EAST,
            Direction.OMNI,
            Direction.SOUTH,
            Direction.EAST,
            Direction.SOUTH_EAST,
        },
        new Direction[] {
            Direction.NORTH_WEST,
            Direction.NORTH_EAST,
            Direction.NORTH,
            Direction.WEST,
            Direction.EAST,
            Direction.OMNI,
            Direction.SOUTH_WEST,
            Direction.SOUTH_EAST,
            Direction.SOUTH,
        },
        new Direction[] {
            Direction.NORTH_EAST,
            Direction.NORTH,
            Direction.EAST,
            Direction.NORTH_WEST,
            Direction.SOUTH_EAST,
            Direction.OMNI,
            Direction.WEST,
            Direction.SOUTH,
            Direction.SOUTH_WEST,
        },
        new Direction[] {
            Direction.NORTH_EAST,
            Direction.SOUTH_EAST,
            Direction.EAST,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.OMNI,
            Direction.NORTH_WEST,
            Direction.SOUTH_WEST,
            Direction.WEST,
        },
        new Direction[] {
            Direction.SOUTH_EAST,
            Direction.EAST,
            Direction.SOUTH,
            Direction.NORTH_EAST,
            Direction.SOUTH_WEST,
            Direction.OMNI,
            Direction.NORTH,
            Direction.WEST,
            Direction.NORTH_WEST,
        },
        new Direction[] {
        },
        new Direction[] {
            Direction.NORTH_WEST,
            Direction.WEST,
            Direction.SOUTH_WEST,
            Direction.SOUTH,
            Direction.SOUTH_EAST,
            Direction.EAST,
            Direction.NORTH_EAST,
            Direction.NORTH,
            Direction.OMNI,
        },
    };
}
