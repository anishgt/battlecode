package team031.actors;

import battlecode.common.*;
import team031.controllers.Controller;
import team031.lookups.AttackerLookups;
import team031.messaging.Message;
import team031.messaging.MessageType;
import team031.messaging.Messager;
import team031.messaging.Relayer;
import team031.nav.Mover;
import team031.update.HostileBuckets;
import team031.update.Sensor;
import team031.util.Constants;
import team031.util.FastIterableLocSet;
import team031.util.FastLocSet;
import team031.util.Math2;

/**
 * Created by jdshen on 1/6/16.
 */
public class Scouter implements Actor {
    private FastIterableLocSet unseen;
    private FastLocSet seen;

    private boolean seenMinX;
    private boolean seenMaxX;
    private boolean seenMinY;
    private boolean seenMaxY;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;

    private int SENSE_WIDTH;
    private Direction bias;

    private MapLocation current;

    private static final int BOX_SIZE = 10;

    private static int calcSenseWidth() {
        Controller c = Controller.c;
        for (int i = 1; i <= c.srs; i++) {
            if (i * i > c.srs) {
                return i - 1;
            }
        }

        return 0;
    }

    public Scouter() {
        unseen = new FastIterableLocSet();
        seen = new FastLocSet();
        SENSE_WIDTH = calcSenseWidth();
        seenMaxX = false;
        seenMaxY = false;
        seenMinX = false;
        seenMinY = false;
        bias = Direction.NORTH;
        for (int i = (3 * Controller.c.id + 2) % 8; i >= 0; i--) {
            bias = bias.rotateRight();
        }
    }

    public static MapLocation toBox(MapLocation loc) {
        return new MapLocation(Math2.floorDivision(loc.x, BOX_SIZE) * BOX_SIZE,
            Math2.floorDivision(loc.y, BOX_SIZE) * BOX_SIZE);
    }

    public MapLocation checkDirection(Direction dir) throws GameActionException {
        int SENSE_WIDTH = this.SENSE_WIDTH;
        RobotController rc = Controller.crc;
        MapLocation here = rc.getLocation();

        if (!rc.onTheMap(here.add(dir, SENSE_WIDTH))) {
            MapLocation loc = here;
            for (int i = 1; i < SENSE_WIDTH; i++) {
                loc = loc.add(dir);
                if (!rc.onTheMap(loc)) {
                    return loc.add(dir.opposite());
                }
            }
        }

        return null;
    }

    // for reference - inline when necessary
    public boolean offMap(int x, int y) {
        return (seenMinX && x < minX) || (seenMinY && y < minY) || (seenMaxX && x > maxX) || (seenMaxY && y > maxY);
    }

    public void checkEdge() throws GameActionException {
        if (!seenMinX) {
            MapLocation k = checkDirection(Direction.WEST);
            if (k != null) {
                seenMinX = true;
                minX = k.x;
            }
        }

        if (!seenMinY) {
            MapLocation k = checkDirection(Direction.NORTH);
            if (k != null) {
                seenMinY = true;
                minY = k.y;
            }
        }

        if (!seenMaxX) {
            MapLocation k = checkDirection(Direction.EAST);
            if (k != null) {
                seenMaxX = true;
                maxX = k.x;
            }
        }

        if (!seenMaxY) {
            MapLocation k = checkDirection(Direction.SOUTH);
            if (k != null) {
                seenMaxY = true;
                maxY = k.y;
            }
        }
    }

    public Direction getDesiredDirection(MapLocation here, MapLocation box) throws GameActionException {
        RobotController rc = Controller.crc;

        if (box.equals(current)) {
            current = null;
        }

        if (current == null) {
            current = unseen.getKey();
        }

        if (current == null) {
            // reset unseen and seen and start over
            unseen = new FastIterableLocSet();
            seen = new FastLocSet();
            return null;
        }

        int x = current.x;
        int y = current.y;
        if ((seenMinX && x < minX) || (seenMinY && y < minY) || (seenMaxX && x > maxX) || (seenMaxY && y > maxY)) {
            seen.add(current);
            unseen.remove(current);
            current = null;
            return null;
        }

        return here.directionTo(current);
    }

    public Message getMessage() {
        RobotController rc = Controller.crc;
        Controller c = Controller.c;
        int hash = Messager.hash(c.id, rc.getRoundNum());
        RobotInfo[] hostile = Sensor.hostile;

        RobotInfo[][] buckets = HostileBuckets.buckets;
        int[] size = HostileBuckets.size;

//
//        if (hostile.length >= Constants.LOTS_OF_ENEMIES) {
//            return null; // focus on surviving.
//        }

        int ZOMBIEDEN = RobotType.ZOMBIEDEN.ordinal();
        if (size[ZOMBIEDEN] > 0) {
            return new Message(MessageType.ZOMBIE_DEN_RELAY, hash, new MapLocation[] {buckets[ZOMBIEDEN][0].location});
        }

        int ARCHON = RobotType.ARCHON.ordinal();
        if (size[ARCHON] > 0) {
            return new Message(MessageType.ARCHON_RELAY, hash, new MapLocation[] {buckets[ARCHON][0].location});
        }

        int TURRET = RobotType.TURRET.ordinal();
        if (size[TURRET] > 0) {
            RobotInfo[] allies = rc.senseNearbyRobots(c.srs, c.team);
            if (allies.length >= Constants.LOTS_OF_ALLIES) {
                return new Message(MessageType.ATTACK_RELAY, hash, new MapLocation[]{buckets[TURRET][0].location});
            }

            return new Message(MessageType.TURRETS_RELAY, hash, new MapLocation[] {buckets[TURRET][0].location});
        }

        int TTM = RobotType.TTM.ordinal();
        if (size[TTM] > 0) {
            return new Message(MessageType.TURRETS_RELAY, hash, new MapLocation[] {buckets[TTM][0].location});
        }

        if (hostile.length > 0) {
            RobotInfo enemy = hostile[0];

            if (enemy.team == c.enemy) {
                if (enemy.attackPower >= 0.1) {
                    return new Message(MessageType.OPP_RELAY, hash,
                        new MapLocation[]{hostile[0].location});
                } else {
                    return null;
                }
            } else {
                return new Message(MessageType.ZOMBIES_RELAY, hash,
                    new MapLocation[] {hostile[0].location});
            }
        }


        RobotInfo[] infos = rc.senseNearbyRobots(c.srs, Team.NEUTRAL);
        if (infos.length > 0) {
            return new Message(MessageType.NEUTRAL_RELAY, hash, new MapLocation[] {infos[0].location});
        }

        MapLocation[] locs = rc.sensePartLocations(c.srs);

        if (locs.length > 0) {
            return new Message(MessageType.PARTS_RELAY, hash, new MapLocation[] {locs[0]});
        }

        return null;
    }

    public void broadcastEnemies() throws GameActionException {
        RobotController rc = Controller.crc;

        // broadcast
        RobotInfo[] hostile = Sensor.hostile;
        if (hostile.length > 0) {
            int size = Math2.min(hostile.length, 1); // only broadcast 1
            MapLocation[] enemies = new MapLocation[size];

            boolean threat = true;

            RobotInfo enemy = AttackerLookups.getClosestEnemy(hostile, rc.getLocation());
            enemies[0] = enemy.location;

            Message m = new Message();
            m.type = MessageType.ENEMIES_AT;
            m.locs = enemies;

            int[] enc = Messager.encode(m);
            rc.broadcastMessageSignal(enc[0], enc[1], Relayer.MINIMUM_RADIUS);
        }

        if (hostile.length > 0) {
            int size = HostileBuckets.size[RobotType.TURRET.ordinal()];
            if (size > 0) {
                int count = Math2.min(Message.MAX_LOCS, size);

                MapLocation[] locs = new MapLocation[count];
                RobotInfo[] buckets = HostileBuckets.buckets[RobotType.TURRET.ordinal()];
                for (int i = count - 1; i >= 0; i--) {
                    locs[i] = buckets[i].location;
                }

                Message m = new Message();
                m.type = MessageType.TURRETS_AT;
                m.locs = locs;

                int[] enc = Messager.encode(m);
                rc.broadcastMessageSignal(enc[0], enc[1], Relayer.MINIMUM_RADIUS);
            }
        }

    }
    @Override
    public boolean  act(boolean move, boolean attack) throws GameActionException {
        checkEdge();

        if (!move) {
            Message message = getMessage();
            if (message != null && (message.type == MessageType.ZOMBIES_RELAY || message.type == MessageType.OPP_RELAY)
                && Relayer.shortBroadcast(message,Constants.BROADCAST_DELAY)) {

                return true;
            }
            if (message != null && Relayer.broadcast(message, Constants.BROADCAST_DELAY)) {
                return true;
            }

            broadcastEnemies();
            return false;
        }

        //localize
        Controller c = Controller.c;
        RobotController rc = Controller.crc;
        MapLocation here = rc.getLocation();

        MapLocation box = toBox(here);
        FastLocSet seen = this.seen;
        FastIterableLocSet unseen = this.unseen;

        boolean seenMinX = this.seenMinX;
        boolean seenMaxX = this.seenMaxX;
        boolean seenMinY = this.seenMinY;
        boolean seenMaxY = this.seenMaxY;
        int minX = this.minX;
        int minY = this.minY;
        int maxX = this.maxX;
        int maxY = this.maxY;

        seen.add(box);
        unseen.remove(box);

        Direction[] dirs = Mover.DIRS_IN_ORDER[bias.ordinal()];
        for (int i = dirs.length - 1; i >= 0; i--) {
            MapLocation add = box.add(dirs[i], BOX_SIZE);
            if (seen.contains(add)) {
                continue;
            }

            int x = add.x;
            int y = add.y;
            if ((seenMinX && x < minX) || (seenMinY && y < minY)
                || (seenMaxX && x > maxX) || (seenMaxY && y > maxY)) {
                seen.add(add);
                unseen.remove(add);
                continue;
            }

            unseen.add(add);

            if (box.equals(current)) {
                current = add;
            }
        }

        RobotInfo[] allies = rc.senseNearbyRobots(c.srs, c.team);
        RobotInfo[] closeAllies = rc.senseNearbyRobots(Constants.SEMI_ADJACENT_SQ, c.team);

        boolean[] canMove = Sensor.canMove;
        RobotInfo[] hostile = Sensor.hostile;

        int[] counts = AttackerLookups.getAttacking(hostile, here);

        RobotInfo turret = Mover.getBestTurret(closeAllies, here);
        if (turret != null) {
            if (here.isAdjacentTo(turret.location)) {
                broadcastEnemies();
                return true;
            }

            // move towards turret
            Direction d = here.directionTo(turret.location);
            Direction dir = Mover.avoidInDir(canMove, counts, d);

            if (dir != null && dir != Direction.OMNI) {
                rc.move(dir);
            }
            return true;
        }

        turret = Mover.getBestTurret(allies, here);
        if (turret != null) {
            if (here.isAdjacentTo(turret.location)) {
                broadcastEnemies();
                return true;
            }

            // move towards turret
            Direction d = here.directionTo(turret.location);
            Direction dir = Mover.avoidInDir(canMove, counts, d);

            if (dir != null && dir != Direction.OMNI) {
                rc.move(dir);
            }
            return true;
        }


        Direction d = getDesiredDirection(here, box);

        // TODO escape to last known safe point
        if (hostile.length > 0) {
            RobotInfo enemy = AttackerLookups.getClosestEnemy(hostile, here);
            if (counts[Direction.OMNI.ordinal()] > 0 || enemy.location.distanceSquaredTo(here) < Constants.SCOUT_ARS) {
                d = enemy.location.directionTo(here);

                // TODO fix if this is bad
                if (enemy.team != Team.ZOMBIE && current != null) {
                    // mark current as done.
                    seen.add(current);
                    unseen.remove(current);
                    current = null;
                }
            }
        }
        if (d == null) {
            return false;
        }

        int[] avoid = AttackerLookups.getAvoidScouts(allies, here);

//        if (avoid[Direction.OMNI.ordinal()] > 0) {
//            if (current != null) {
//                seen.add(current);
//                unseen.remove(current);
//                current = null;
//            }
//        }

        AttackerLookups.sum(counts, avoid);

        Direction dir = Mover.avoidInDir(canMove, counts, d);

        if (dir != null && dir != Direction.OMNI) {
            rc.move(dir);
        } else {
            // can't move, delete current.
            if (current != null) {
                seen.add(current);
                unseen.remove(current);
            }
            current = null;
        }

        return false;
    }
}
