package team031.nav;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team031.DirectionOrdinals;
import team031.controllers.Controller;
import team031.util.FastLocDirSet;

/**
 * Created by jdshen on 1/9/15.
 */
public class Bugger {
    public MapLocation start;
    public MapLocation from;
    public MapLocation cur;
    public MapLocation to;

    public boolean hugging;
    public boolean wentLeft;
    public boolean hugLeft;

    public Direction hugDir;
    public FastLocDirSet seen;
    public boolean recursed;
    public boolean bugging;

    public Direction preDir;
    public boolean useMap;

    public Direction lastDir;

    public Bugger() {
        seen = new FastLocDirSet();
        bugging = false;
        preDir = Direction.NONE;
    }

    public void endBug() {
        bugging = false;
    }

    public void startBug(MapLocation loc) {
        RobotController rc = Controller.crc;
        start = rc.getLocation();
        from = rc.getLocation();
        to = loc;
        hugging = false;
        hugLeft = true;
        wentLeft = true;
        bugging = true;
        useMap = true;
    }

    // Something went wrong during, restart the bugging.
    private void restart(MapLocation loc) {
        from = Controller.crc.getLocation();
        to = loc;
        hugging = false;
        hugLeft = true;
        seen.clear();
    }

    public Direction bug(boolean[] canMove) throws GameActionException {
        Controller.crc.setIndicatorDot(to, 0, 255, 0);
        cur = Controller.crc.getLocation();

        // localize
        MapLocation cur = this.cur;

        Direction desiredDir = cur.directionTo(to);
        if (desiredDir == Direction.NONE || desiredDir == Direction.OMNI) {
            return desiredDir;
        }

        // desiredDir if possible, else left, else right. Makes dist smaller
        Direction bestDir = goInDir(canMove, desiredDir);

        if (hugging) {
            // been here before, restart hugging
            if (seen.contains(cur, hugDir)) {
                hugging = false;
            }

            if (bestDir != null) {
                if (canMove[bestDir.ordinal()] && cur.distanceSquaredTo(to) < from.distanceSquaredTo(to)) {
                    hugging = false;
                    return bestDir;
                }
            }

            seen.add(cur, hugDir);
            return hug(canMove);
        } else {
            if (bestDir != null) {
                return bestDir;
            }

            // start hugging
            seen.clear();
            hugging = true;
            from = cur;
            hugDir = desiredDir;
            hugLeft = wentLeft;
            recursed = false;
            return hug(canMove);
        }
    }

    public Direction goInDir(boolean[] canMove, Direction desiredDir){
        if (canMove[desiredDir.ordinal()]) {
            return desiredDir;
        }

        Direction left = desiredDir.rotateLeft();
        Direction right = desiredDir.rotateRight();
        boolean leftIsBetter = (cur.add(left).distanceSquaredTo(to) <
            cur.add(right).distanceSquaredTo(to));
        if (leftIsBetter) {
            if (canMove[left.ordinal()]) {
                wentLeft = true;
                return left;
            }

            if (canMove[right.ordinal()]) {
                wentLeft = false;
                return right;
            }
        } else {
            if (canMove[right.ordinal()]) {
                wentLeft = false;
                return right;
            }

            if (canMove[left.ordinal()]) {
                wentLeft = true;
                return left;
            }
        }

        return null;
    }

    private Direction handleOffMap(boolean[] canMove) throws GameActionException {
        if (!recursed) {
            seen.clear();
            hugLeft = !hugLeft;
            recursed = true;
            return hug(canMove);
        } else {
            //something went wrong, reset
            restart(to);
            return Direction.NONE;
        }
    }

    private Direction hug(boolean[] canMove) throws GameActionException {
        Direction tryDir;
        MapLocation tryLoc;
        int i;
        MapLocation cur = this.cur;
        RobotController rc = Controller.crc;

        if (hugLeft) {
            tryDir = hugDir.rotateLeft();
            tryLoc = cur.add(tryDir);
            for (i = 0; i < 8 && !canMove[tryDir.ordinal()]; i++) {
                if (!rc.onTheMap(tryLoc)) {
                    return handleOffMap(canMove);
                }

                tryDir = tryDir.rotateLeft();
                tryLoc = cur.add(tryDir);
            }
        } else {
            tryDir = hugDir.rotateRight();
            tryLoc = cur.add(tryDir);
            for (i = 0; i < 8 && !canMove[tryDir.ordinal()]; i++) {
                if (!rc.onTheMap(tryLoc)) {
                    return handleOffMap(canMove);
                }

                tryDir = tryDir.rotateRight();
                tryLoc = cur.add(tryDir);
            }
        }

        if (i == 8) {
            //blocked in all directions
            return Direction.NONE;
        } else {
            hugDir = hugLeft ?
                DirectionOrdinals.updateHugDirLeft[tryDir.ordinal()] :
                DirectionOrdinals.updateHugDirRight[tryDir.ordinal()];
            return tryDir;
        }
    }
}
