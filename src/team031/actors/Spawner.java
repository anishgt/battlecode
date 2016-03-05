package team031.actors;

import battlecode.common.*;
import team031.controllers.Controller;
import team031.lookups.AttackerLookups;
import team031.messaging.Message;
import team031.messaging.MessageType;
import team031.messaging.Messager;
import team031.nav.Bugger;
import team031.nav.Mover;
import team031.update.Archon;
import team031.update.Sensor;
import team031.update.Signals;
import team031.update.TurretTracker;
import team031.util.Constants;

/**
 * Created by jdshen on 1/6/16.
 */
public class Spawner implements Actor {
    public boolean waiting;
    public int[] spawned;
    public WayPoint to;
    public Bugger bugger;
    public RobotType[] buildOrder;

    int buildTurn = 0;


    public Spawner() {
        spawned = Archon.spawned; // store a reference

        bugger = new Bugger();
        setPoint(new WayPoint(Mover.getArchon(), MessageType.HELP));
        waiting = true;
        buildOrder = new RobotType[]{
            RobotType.SCOUT,
            RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER,
            RobotType.VIPER,
            RobotType.SCOUT,
            RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER, RobotType.SOLDIER,
            RobotType.TURRET,
        };
    }

    public RobotType nextUnit() {
        return buildOrder[buildTurn % buildOrder.length];
    }

    public boolean spawnScout() throws GameActionException {
        RobotController rc = Controller.crc;
        // spawn a scout, anywhere
        if (rc.getTeamParts() < RobotType.SCOUT.partCost) {
            return false;
        }

        Direction dir = Direction.NORTH;
        for (int i = 7; i >= 0; i--, dir = dir.rotateRight()) {
            if (rc.canBuild(dir, RobotType.SCOUT)) {
                spawn(dir, RobotType.SCOUT, MessageType.ACTOR);
                return true;
            }
        }

        return false;
    }

    public boolean spawnViper() throws GameActionException {
        RobotController rc = Controller.crc;

        // spawn a scout, anywhere
        if (rc.getTeamParts() < RobotType.VIPER.partCost) {
            return false;
        }

        Direction dir = Direction.NORTH;
        for (int i = 7; i >= 0; i--, dir = dir.rotateRight()) {
            if (rc.canBuild(dir, RobotType.VIPER)) {
                spawn(dir, RobotType.VIPER, MessageType.ACTOR);
                return true;
            }
        }

        return false;
    }


    public boolean spawnSoldier() throws GameActionException {
        RobotController rc = Controller.crc;

        // spawn a scout, anywhere
        if (rc.getTeamParts() < RobotType.SOLDIER.partCost) {
            return false;
        }

        Direction dir = Direction.NORTH;
        for (int i = 7; i >= 0; i--, dir = dir.rotateRight()) {
            if (rc.canBuild(dir, RobotType.SOLDIER)) {
                spawn(dir, RobotType.SOLDIER, MessageType.ACTOR);
                return true;
            }
        }

        return false;
    }

    public boolean spawnTurret() throws GameActionException {
        RobotController rc = Controller.crc;
        Controller c = Controller.c;
        MapLocation here = rc.getLocation();

        // spawn a scout, anywhere
        if (rc.getTeamParts() < RobotType.TURRET.partCost) {
            return false;
        }


        Direction dir = Direction.NORTH;
        for (int i = 7; i >= 0; i--, dir = dir.rotateRight()) {
            if (rc.canBuild(dir, RobotType.TURRET)) {
                spawn(dir, RobotType.TURRET, MessageType.ACTOR);
                return true;
            }
        }

        return false;
    }

    public boolean spawn() throws GameActionException {
        RobotType next = nextUnit();
        switch (next) {
            case SCOUT:
                return spawnScout();
            case SOLDIER:
                return spawnSoldier();
            case GUARD:
                break;
            case VIPER:
                return spawnViper();
            case TURRET:
                return spawnTurret();
            default:
                break;
        }
        return false;
    }

    public void setPoint(WayPoint to) {
        this.to = to;
        bugger.startBug(to.to);
        waiting = false;
    }

    public void stop() {
        bugger.endBug();
        waiting = true;
    }

    public void sense() {
        RobotController rc = Controller.crc;
        Controller c = Controller.c;
        MapLocation here = rc.getLocation();
        MapLocation[] locs = rc.sensePartLocations(c.srs);

        RobotInfo[] infos = rc.senseNearbyRobots(c.srs, Team.NEUTRAL);
        for (int i = infos.length - 1; i >= 0; i--) {
            if (waiting || infos[i].location.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(infos[i].location, MessageType.NEUTRAL_RELAY));
            }
        }

        if (!waiting && to.type.ordinal() < MessageType.PARTS_RELAY.ordinal()) {
            return;
        }

        for (int i = locs.length - 1; i >= 0; i--) {
            if (waiting || locs[i].distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(locs[i], MessageType.PARTS_RELAY));
            }
        }

    }

    public void read() {
        MapLocation here = Controller.crc.getLocation();

        int[] size = Signals.size;
        Signal[][] buckets = Signals.buckets;

        int NEUTRAL_RELAY = MessageType.NEUTRAL_RELAY.ordinal();
        for (int i = size[NEUTRAL_RELAY] - 1; i >= 0; i--) {
            int[] enc = buckets[NEUTRAL_RELAY][i].getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            MapLocation loc = m.locs[0];
            if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(loc, MessageType.PARTS_RELAY));
            }

            return;
        }

        if (!waiting && to.type.ordinal() < MessageType.PARTS_RELAY.ordinal()) {
            return;
        }

        int PARTS_RELAY = MessageType.PARTS_RELAY.ordinal();
        for (int i = size[PARTS_RELAY] - 1; i >= 0; i--) {
            int[] enc = buckets[PARTS_RELAY][i].getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            MapLocation loc = m.locs[0];
            if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(loc, MessageType.PARTS_RELAY));
            }
        }
    }

    // return true if done moving
    public boolean activate() throws GameActionException {
        RobotController rc = Controller.crc;
        MapLocation there = this.to.to;
        MapLocation here = rc.getLocation();

        if (to.type == MessageType.PARTS_RELAY) {
            if (rc.canSenseLocation(there) && rc.senseParts(there) <= 0) {
                stop();
                return false;
            }

            if (here.equals(there)) {
                stop();
                return false;
            }
        } else {
            if (rc.canSenseLocation(there)) {
                RobotInfo info = rc.senseRobotAtLocation(there);
                if (info == null || info.team != Team.NEUTRAL) {
                    stop();
                    return false;
                }
                if (here.isAdjacentTo(there)) {
                    rc.activate(there);
                    Message m = new Message();
                    m.type = MessageType.ACTOR;
                    m.locs = new MapLocation[] { there }; // have them center on themself
                    int[] enc = Messager.encode(m);
                    rc.broadcastMessageSignal(enc[0], enc[1], Constants.ADJACENT_SQ);
                    stop();
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public boolean act(boolean move, boolean attack) throws GameActionException {
        RobotController rc = Controller.crc;
        MapLocation here = rc.getLocation();

        if (!move) {
            // mark all locations as seen
            return false;
        }

        // enemy is close, retreat.
        RobotInfo[] hostile = Sensor.hostile;
        boolean[] canMove = Sensor.canMove;
        Controller c = Controller.c;
        int[] attacking = AttackerLookups.getAttacking(hostile, rc.getLocation());


        if (hostile.length > 0) {
            RobotInfo enemy = AttackerLookups.getClosestEnemy(hostile, here);
            if (enemy.attackPower > 0.1 && enemy.location.distanceSquaredTo(here) <= c.ars) {
                Direction desired = enemy.location.directionTo(here);
                Direction dir = Mover.avoidInDir(canMove, attacking, desired);

                if (dir != null && dir != Direction.OMNI && dir != Direction.NONE) {
                    rc.move(dir);
                    bugger.endBug();
                    return true;
                }
            }
        }

        read();
        sense();

        if (waiting || here.distanceSquaredTo(to.to) >= c.srs * 2 || to.type == MessageType.PARTS_RELAY) {
            boolean spawned = spawn();
            if (spawned) {
                return true;
            }
        }

        if (waiting) {
            // todo move somewhere useful or safe.
            return false;
        }

        if (!bugger.bugging) {
            bugger.startBug(to.to);
        }

        boolean activated = activate();
        if (activated) {
            return true;
        }

        MapLocation[] turrets = TurretTracker.getTurrets();
        int[] turretCount = AttackerLookups.getAttacking(turrets, here);

        boolean[] avoidMove = Mover.canAvoidMove(canMove, turretCount, attacking);

        Direction went = here.isAdjacentTo(to.to) ?
            Mover.adjacentClear(here.directionTo(to.to), avoidMove) :
            Mover.moveOrClear(here.directionTo(to.to), avoidMove);
        if (went != null) {
            bugger.endBug();
        } else {
            Direction dir = bugger.bug(canMove);

            if (dir != null && dir != Direction.OMNI && dir != Direction.NONE) {
                rc.move(dir);
                return true;
            }

            bugger.endBug();
            return false;
        }

        return true;
    }


    public void spawn(Direction d, RobotType t, MessageType t2) throws GameActionException {
        Message m = new Message();
        m.type = t2;
        spawn(d, t, m);
    }

    public void spawn(Direction d, RobotType t, Message m) throws GameActionException {
        RobotController rc = Controller.crc;
        spawned[t.ordinal()]++;
        buildTurn++;
        rc.build(d, t);
        int[] enc = Messager.encode(m);
        rc.broadcastMessageSignal(enc[0], enc[1], Constants.ADJACENT_SQ);
    }
}
