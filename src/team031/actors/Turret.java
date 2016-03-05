package team031.actors;

import battlecode.common.*;
import team031.controllers.Controller;
import team031.lookups.AttackerLookups;
import team031.messaging.Message;
import team031.messaging.MessageType;
import team031.messaging.Messager;
import team031.nav.Bugger;
import team031.nav.Mover;
import team031.update.Sensor;
import team031.update.Signals;
import team031.update.TurretTracker;
import team031.util.Constants;
import team031.util.Math2;

/**
 * Final micro for soldier, modify this only.
 */
public class Turret implements Actor {
    private WayPoint to;
    private Bugger bugger;
    private boolean waiting;

    public Turret() {
        bugger = new Bugger();
        setPoint(new WayPoint(Mover.getArchon(), MessageType.HELP));
        waiting = true;
    }

    public void read() {
        Signal[] sigs = Signals.sigs;
        Team team = Controller.c.team;
        RobotController rc = Controller.crc;
        MapLocation here = Controller.crc.getLocation();

        Signal[][] buckets = Signals.buckets;
        int[] size = Signals.size;

        // respond to help immediately
        int HELP = MessageType.HELP.ordinal();
        if (size[HELP] > 0) {
            Signal sig = buckets[HELP][0];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            setPoint(new WayPoint(m.locs[0], MessageType.HELP));
            return;
        }

        // respond to help immediately
        if (!waiting && to.type.ordinal() < MessageType.ATTACK_RELAY.ordinal()) {
            return;
        }

        int ATTACK_RELAY = MessageType.ATTACK_RELAY.ordinal();
        if (size[ATTACK_RELAY] > 0) {
            Signal sig = buckets[ATTACK_RELAY][0];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            setPoint(new WayPoint(m.locs[0], MessageType.ATTACK_RELAY));
            return;
        }

        if (!waiting && to.type.ordinal() < MessageType.ZOMBIE_DEN_RELAY.ordinal()) {
            return;
        }

        int ZOMBIE_DEN_RELAY = MessageType.ZOMBIE_DEN_RELAY.ordinal();
        for (int i = Math2.min(size[ZOMBIE_DEN_RELAY], Constants.MAX_DECISIONS) - 1; i >= 0; i--) {
            Signal sig = buckets[ZOMBIE_DEN_RELAY][i];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            MapLocation loc = m.locs[0];

            if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(m.locs[0], MessageType.ZOMBIE_DEN_RELAY));
            }
        }

        if (!waiting && to.type.ordinal() < MessageType.ZOMBIES_RELAY.ordinal()) {
            return;
        }

        int ZOMBIES_RELAY = MessageType.ZOMBIES_RELAY.ordinal();
        for (int i = Math2.min(size[ZOMBIES_RELAY], Constants.MAX_DECISIONS) - 1; i >= 0; i--) {
            Signal sig = buckets[ZOMBIES_RELAY][i];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            MapLocation loc = m.locs[0];

            if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(m.locs[0], MessageType.ZOMBIES_RELAY));
            }
        }

        if (!waiting && to.type.ordinal() < MessageType.OPP_RELAY.ordinal()) {
            return;
        }

        int OPP_RELAY = MessageType.OPP_RELAY.ordinal();
        for (int i = Math2.min(size[OPP_RELAY], Constants.MAX_DECISIONS) - 1; i >= 0; i--) {
            Signal sig = buckets[OPP_RELAY][i];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            MapLocation loc = m.locs[0];

            if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(m.locs[0], MessageType.OPP_RELAY));
            }
        }

        if (!waiting && to.type.ordinal() < MessageType.ARCHON_RELAY.ordinal()) {
            return;
        }

        int ARCHON_RELAY = MessageType.ARCHON_RELAY.ordinal();
        for (int i = Math2.min(size[ARCHON_RELAY], Constants.MAX_DECISIONS) - 1; i >= 0; i--) {
            Signal sig = buckets[ARCHON_RELAY][i];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            MapLocation loc = m.locs[0];

            if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(m.locs[0], MessageType.ARCHON_RELAY));
            }
        }

        if (!waiting && to.type.ordinal() < MessageType.TURRETS_RELAY.ordinal()) {
            return;
        }

        int TURRETS_RELAY = MessageType.TURRETS_RELAY.ordinal();
        for (int i = Math2.min(size[TURRETS_RELAY], Constants.MAX_DECISIONS) - 1; i >= 0; i--) {
            Signal sig = buckets[TURRETS_RELAY][i];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            MapLocation loc = m.locs[0];

            if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
                setPoint(new WayPoint(m.locs[0], MessageType.TURRETS_RELAY));
            }
        }

        // TODO more than just zombies (OPP, and turrets and shit)
    }

    public void setPoint(WayPoint to) {
        if (this.to == null || !this.to.equals(to)) {
            this.to = to;
            bugger.startBug(to.to);
            waiting = false;
        }
    }

    public MapLocation findEnemy() {
        int ENEMIES_AT = MessageType.ENEMIES_AT.ordinal();
        Signal[] buckets = Signals.buckets[ENEMIES_AT];
        int size = Signals.size[ENEMIES_AT];
        MapLocation here = Controller.crc.getLocation();

        MapLocation loc = null;
        int min = 20000; // large enough
        for (int i = Math2.min(Constants.MAX_ENEMIES / 2, size) - 1; i >= 0; i--) {
            Signal sig = buckets[i];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            int dist = m.locs[0].distanceSquaredTo(here);
            if (dist < min && dist >= GameConstants.TURRET_MINIMUM_RANGE) {
                loc = m.locs[0];
                min = dist;
            }
        }

        RobotInfo[] hostile = Sensor.hostile;
        for (int i = Math2.min(Constants.MAX_ENEMIES / 2, hostile.length) - 1; i >= 0; i--) {
            int dist = hostile[i].location.distanceSquaredTo(here);
            if (dist < min && dist >= GameConstants.TURRET_MINIMUM_RANGE) {
                loc = hostile[i].location;
                min = dist;
            }
        }

        return loc;
    }

    public boolean walk(boolean move, boolean attack, int[] attacking, int[] turretCount) throws GameActionException {
        // no target, move towards to
        if (!move) {
            return false;
        }

        RobotController rc = Controller.crc;
        RobotInfo[] hostile = Sensor.hostile;
        boolean[] canMove = Sensor.canMove;
        if (waiting) {
            return false;
        }

        MapLocation here = rc.getLocation();

        if (here.distanceSquaredTo(to.to) <= Controller.c.ars && hostile.length <= 0) {
            bugger.endBug();
            waiting = true;
            return false;
        }

        if (!bugger.bugging) {
            bugger.startBug(to.to);
        }

        boolean[] avoidMove = Mover.canAvoidMove(canMove, attacking, turretCount);

        Direction dir = bugger.bug(avoidMove);

        if (dir != null && dir != Direction.OMNI && dir != Direction.NONE) {
            rc.move(dir);
            return true;
        }

        bugger.endBug();
        return false;
    }

    @Override
    public boolean act(boolean move, boolean attack) throws GameActionException {
        read();

        if (!move && !attack) {
            return false;
        }

        RobotController rc = Controller.crc;
        Controller c = Controller.c;
        MapLocation here = rc.getLocation();

        RobotInfo[] hostile = Sensor.hostile;
        boolean[] canMove = Sensor.canMove;

        boolean ttm = rc.getType() == RobotType.TTM;

        MapLocation[] turrets = TurretTracker.getTurrets();
        int[] turretCount = AttackerLookups.getAttacking(turrets, here);
        int OMNI = Direction.OMNI.ordinal();

        Direction hereTo = here.directionTo(to.to);
        int[] attacking = AttackerLookups.getAttacking(hostile, here);

        MapLocation loc = findEnemy();

        // no nearby enemy
        if (loc == null || loc.distanceSquaredTo(here) > RobotType.SCOUT.sensorRadiusSquared) {
            if (!ttm) {
                if (move) {
                    rc.pack();
                }
                return true;
            } else {
                return walk(move, attack, attacking, turretCount);
            }
        }

        // otherwise, there is an enemy
        if (ttm) {
            if (move) {
                rc.unpack();
            }
            return true;
        } else {
            if (attack && rc.canAttackLocation(loc)) {
                rc.attackLocation(loc);
            }
            return true;
        }
    }

}
