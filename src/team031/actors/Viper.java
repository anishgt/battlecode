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
public class Viper implements Actor {
    private WayPoint to;
    private Bugger bugger;
    private boolean waiting;

    private int ignoreTurrets;

    // TODO previous waypoints, after finishing current one, go backwards to old waypoints

    public Viper() {
        bugger = new Bugger();
        setPoint(new WayPoint(Mover.getArchon(), MessageType.HELP));
        waiting = true;
        ignoreTurrets = -1;
    }

    public void read() {
        Signal[] sigs = Signals.sigs;
        Team team = Controller.c.team;
        RobotController rc = Controller.crc;
        MapLocation here = Controller.crc.getLocation();

        Signal[][] buckets = Signals.buckets;
        int[] size = Signals.size;

//        if (team == Team.B) {
//            // respond to help immediately
//            int HELP = MessageType.HELP.ordinal();
//            if (size[HELP] > 0) {
//                Signal sig = buckets[HELP][0];
//                int[] enc = sig.getMessage();
//                Message m = Messager.decode(enc[0], enc[1]);
//                setPoint(new WayPoint(m.locs[0], MessageType.HELP));
//                return;
//            }
//        }

        if (!waiting && to.type.ordinal() < MessageType.ATTACK_RELAY.ordinal()) {
            return;
        }

        int ATTACK_RELAY = MessageType.ATTACK_RELAY.ordinal();
        if (size[ATTACK_RELAY] > 0) {
            ignoreTurrets = rc.getRoundNum() + Constants.ATTACK_ROUNDS;
            Signal sig = buckets[ATTACK_RELAY][0];
            int[] enc = sig.getMessage();
            Message m = Messager.decode(enc[0], enc[1]);
            setPoint(new WayPoint(m.locs[0], MessageType.ATTACK_RELAY));
            return;
        }

//        if (team == Team.B) {
//            if (!waiting && to.type.ordinal() < MessageType.ZOMBIE_DEN_RELAY.ordinal()) {
//                return;
//            }
//
//            int ZOMBIE_DEN_RELAY = MessageType.ZOMBIE_DEN_RELAY.ordinal();
//            for (int i = Math2.min(size[ZOMBIE_DEN_RELAY], Constants.MAX_DECISIONS) - 1; i >= 0; i--) {
//                Signal sig = buckets[ZOMBIE_DEN_RELAY][i];
//                int[] enc = sig.getMessage();
//                Message m = Messager.decode(enc[0], enc[1]);
//                MapLocation loc = m.locs[0];
//
//                if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
//                    setPoint(new WayPoint(m.locs[0], MessageType.ZOMBIE_DEN_RELAY));
//                }
//            }
//
//            if (!waiting && to.type.ordinal() < MessageType.ZOMBIES_RELAY.ordinal()) {
//                return;
//            }
//
//            int ZOMBIES_RELAY = MessageType.ZOMBIES_RELAY.ordinal();
//            for (int i = Math2.min(size[ZOMBIES_RELAY], Constants.MAX_DECISIONS) - 1; i >= 0; i--) {
//                Signal sig = buckets[ZOMBIES_RELAY][i];
//                int[] enc = sig.getMessage();
//                Message m = Messager.decode(enc[0], enc[1]);
//                MapLocation loc = m.locs[0];
//
//                if (waiting || loc.distanceSquaredTo(here) < to.to.distanceSquaredTo(here)) {
//                    setPoint(new WayPoint(m.locs[0], MessageType.ZOMBIES_RELAY));
//                }
//            }
//        }

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

    public boolean walk(boolean move, boolean attack, int[] attacking, int[] turretCount) throws GameActionException {
        // no target, move towards to
        if (!move) {
            return false;
        }

        RobotController rc = Controller.crc;
        RobotInfo[] hostile = Sensor.hostile;
        boolean[] canMove = Sensor.canMove;
        if (waiting) {
//            if (hostile.length <= 0) {
//                // move outward a bit.
//                Direction dir = to.directionTo(Controller.crc.getLocation());
//                if (dir == Direction.OMNI) {
//                    dir = Direction.NORTH;
//                }
//
//                boolean[] tetheredMove = Mover.calcTetheredMove(to, Controller.c.ars, canMove);
//                Direction went = Mover.moveOrClear(dir, 50, tetheredMove);
//            }

            return false;
        }

        MapLocation here = rc.getLocation();

        if (here.distanceSquaredTo(to.to) < Controller.c.ars && hostile.length <= 0) {
            bugger.endBug();
            waiting = true;
            return false;
        }

        if (!bugger.bugging) {
            bugger.startBug(to.to);
        }

        boolean[] avoidMove = Mover.canAvoidMove(canMove, attacking, turretCount);

        Direction went = Mover.moveOrClear(here.directionTo(to.to), avoidMove);
        if (went != null) {
            bugger.endBug();
        } else {
            Direction dir = bugger.bug(avoidMove);

            if (dir != null && dir != Direction.OMNI && dir != Direction.NONE) {
                rc.move(dir);
                return true;
            }

            bugger.endBug();
            return false;
        }
        return true;
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

        MapLocation[] turrets = ignoreTurrets >= rc.getRoundNum() ? new MapLocation[]{} : TurretTracker.getTurrets();
        int[] turretCount = AttackerLookups.getAttacking(turrets, here);
        int OMNI = Direction.OMNI.ordinal();

        Direction hereTo = here.directionTo(to.to);

        // avoid turrets
        // TODO if can't avoid turret, move away at least.
        if (turretCount[OMNI] > 0) {
            Direction dir = Mover.avoidInDir(canMove, turretCount, hereTo);
            if (dir != null && dir != Direction.OMNI && turretCount[dir.ordinal()] < turretCount[OMNI]) {
                if (move) {
                    bugger.endBug(); // TODO remove when bugger is fixed
                    rc.move(dir);
                }

                return true;
            }
        }

        int[] attacking = AttackerLookups.getAttacking(hostile, here);

        if (hostile.length <= 0) {
            return walk(move, attack, attacking, turretCount);
        }

        RobotInfo[] hostileARS = Sensor.hostileARS;
        RobotInfo enemyInfo = AttackerLookups.getClosestEnemy(hostile, here);
        MapLocation enemy = enemyInfo.location;
        Direction enemyHere = enemy.directionTo(here);

        // something in attack range
        if (hostileARS.length > 0) {
            if (enemyInfo.attackPower < 0.1) {
                // only enemy does no damage. move in closer if its a zombie den
                if (enemyInfo.type == RobotType.ZOMBIEDEN) {
                    int[] adj = AttackerLookups.getAttacking(enemy, here, Constants.ADJACENT_SQ);
                    Direction dir = Mover.avoidInDir(canMove, adj, turretCount, enemyHere.opposite());
                    int distHere = here.distanceSquaredTo(enemy);
                    if (dir != null && dir != Direction.OMNI) {
                        int distDir = here.add(dir).distanceSquaredTo(enemy);
                        if (distDir < distHere) {
                            if (move) {
                                bugger.endBug(); // TODO remove when bugger is fixed
                                rc.move(dir);
                            }

                            return true;
                        }
                    }
                }
            } else if (enemy.distanceSquaredTo(here) <= Constants.SEMI_ADJACENT_SQ) {
                // something is within your inner range, move back if they are too close and you can stay in range
                int[] attackable = AttackerLookups.getAttackable(hostile, here, c.ars);
                Direction dir = Mover.stayInDir(canMove, attacking, attackable, turretCount, enemyHere);

                if (dir != null && dir != Direction.OMNI) {
                    if (move) {
                        bugger.endBug(); // TODO remove when bugger is fixed
                        rc.move(dir);
                    }

                    return true;
                }
            }
        }

        // pick the first enemy, and move away from them.
        // ONLY if moving back can help - i.e. >= 2 people who can hit only you.
        if (attacking[Direction.OMNI.ordinal()] > 0) {
            int[] focused = AttackerLookups.getFocused(hostile, here, rc);
            Direction dir = Mover.avoidInDir(canMove, focused, turretCount, enemyHere);

            if (dir != null && dir != Direction.OMNI && focused[dir.ordinal()] <= focused[OMNI] - 2) {
                if (move) {
                    bugger.endBug();
                    rc.move(dir);
                }

                // stay in position, but wait til cooldown
                return true;
            }

            // can't move safely, fall down to attacking
        }

        // choose a target
        if (hostileARS.length > 0) {
            RobotInfo targetInfo = AttackerLookups.getClosestUninfected(hostileARS, here);
            MapLocation target = targetInfo.location;
            if (attack) {
                if (target.distanceSquaredTo(here) <= c.ars) {
                    rc.attackLocation(target);
                } else {
                    rc.attackLocation(hostileARS[0].location);
                }
                return true;
            } else {
                return true; // wait a turn
            }
        }

        // move in if enemies are occupied
        if (hostile.length > 0) {
            Direction hereEnemy = here.directionTo(enemy);
            int[] idle = AttackerLookups.getIdle(hostile, here, rc);

            // TODO make this move in if <= 1
            Direction dir = Mover.avoidInDir(canMove, idle, turretCount, hereEnemy);
            if (dir != null && dir != Direction.OMNI) {
                if (move) {
                    bugger.endBug();
                    rc.move(dir);
                }

                // stay in position, but wait til cooldown
                return true;
            }
        }

        return walk(move, attack, attacking, turretCount);
    }

}
