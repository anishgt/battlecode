package team031.archon;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Signal;
import team031.controllers.Controller;
import team031.messaging.Message;
import team031.messaging.MessageType;
import team031.messaging.Messager;
import team031.messaging.Relayer;
import team031.update.Sensor;
import team031.update.Signals;
import team031.util.Constants;
import team031.util.FastIterableLocSet;

/**
 * Created by jdshen on 1/22/16.
 */
public class ThreadManager {
    public ObjectiveThread[] threads;

    // dens that are still alive
    public FastIterableLocSet dens;

    // enemy location
    public FastIterableLocSet opp;

    public ThreadManager() {
        dens = new FastIterableLocSet();
        threads = new ObjectiveThread[4]; // 4 control groups.
        for (int i = 3; i >= 0; i--) {
            threads[i] = new ObjectiveThread(MessageType.IDLE, Controller.crc.getLocation());
        }
    }

    public void read() {
        Signal[][] buckets = Signals.buckets;
        int[] size = Signals.size;

        int ZOMBIE_DEN_RELAY = MessageType.ZOMBIE_DEN_RELAY.ordinal();
        for (int i = size[ZOMBIE_DEN_RELAY] - 1; i >= 0; i--) {
            int[] enc = buckets[ZOMBIE_DEN_RELAY][i].getMessage();
            Message m = Messager.decode(enc[0], enc[1]);

            dens.add(m.locs[0]);
        }

        // TODO handle opponent, maybe do something with archon ownership

        int HELP_ARCHON = MessageType.HELP_ARCHON.ordinal();
        for (int i = size[HELP_ARCHON] - 1; i >= 0; i--) {
            int[] enc = buckets[HELP_ARCHON][i].getMessage();
            Message m = Messager.decode(enc[0], enc[1]);

            threads[m.group].reset(m);
        }

        int ATTACK_DEN = MessageType.ATTACK_DEN.ordinal();
        for (int i = size[ATTACK_DEN] - 1; i >= 0; i--) {
            int[] enc = buckets[ATTACK_DEN][i].getMessage();
            Message m = Messager.decode(enc[0], enc[1]);

            threads[m.group].reset(m);

            // don't attack twice
            dens.remove(m.locs[0]);
        }

        int ATTACK_ZOMBIES = MessageType.ATTACK_ZOMBIES.ordinal();
        for (int i = size[ATTACK_ZOMBIES] - 1; i >= 0; i--) {
            int[] enc = buckets[ATTACK_ZOMBIES][i].getMessage();
            Message m = Messager.decode(enc[0], enc[1]);

            threads[m.group].reset(m);
        }

        int IDLE = MessageType.IDLE.ordinal();
        for (int i = size[IDLE] - 1; i >= 0; i--) {
            int[] enc = buckets[IDLE][i].getMessage();
            Message m = Messager.decode(enc[0], enc[1]);

            threads[m.group].reset(m);

            dens.remove(m.locs[0]);
        }
    }

    public int requestSingle(Message m) throws GameActionException {
        int min = 100; // big enough
        int j = -1;
        for (int i = 3; i >= 0; i--) {
            if (threads[i].type.ordinal() < min) {
                min = threads[i].type.ordinal();
                j = i;
            }
        }

        if (j == -1 || min <= m.type.ordinal()) {
            return -1;
        }

        threads[j].take(m);
        return j;
    }

    public void broadcastHelp() throws GameActionException {
        Controller c = Controller.c;
        RobotController rc = Controller.crc;
        MapLocation here = rc.getLocation();

        // check if already issued
        MessageType HELP_ARCHON = MessageType.HELP_ARCHON;
        for (int i = 3; i >= 0; i--) {
            if (threads[i].control && threads[i].type == HELP_ARCHON) {
                return;
            }
        }

        // not issued, request for help
        int hash = Messager.hash(c.id, rc.getRoundNum());
        Message m = new Message(MessageType.HELP_ARCHON, hash, new MapLocation[] {here});

        int j = requestSingle(m);
        if (j >= 0) {
            m.group = j;
            Relayer.archonBroadcast(m);
        }
    }

    public void broadcast() throws GameActionException {
        // see if need help
        boolean help = Sensor.hostileARS.length > Constants.LOTS_OF_ENEMIES;
        if (help) {
            broadcastHelp();
            // request failed
        } else {
            // check if issued, and now switch to idle.
            MessageType HELP_ARCHON = MessageType.HELP_ARCHON;
            for (int i = 3; i >= 0; i--) {
                if (threads[i].control && threads[i].type == HELP_ARCHON) {
                    return;
                }
            }
        }

    }

    public void broadcastDen() throws GameActionException {
        // TODO maybe try this if regular relays dont work
//        if (bugger.bugging) {
//            return;
//        }
//
//        // TODO make more sophisticated, avoid places close to enemy archons, etc.
//        if (curDen != null && !curDen.equals(to)) {
//            RobotController rc = Controller.crc;
//            Controller c = Controller.c;
//            int hash = Messager.hash(c.id, rc.getRoundNum());
//            Message m = new Message(MessageType.ATTACK_DEN, hash, new MapLocation[] {curDen});
//            int[] enc = Messager.encode(m);
//
//            setTo(curDen);
//            rc.broadcastMessageSignal(enc[0], enc[1], MINIMUM_RADIUS_SQ);
//        }
    }


}
