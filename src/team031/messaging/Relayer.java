package team031.messaging;

import battlecode.common.*;
import team031.controllers.Controller;
import team031.util.Constants;
import team031.util.Math2;

/**
 * Created by jdshen on 1/11/16.
 */
public class Relayer {
    public static final boolean[] seen = new boolean[Messager.ID_MOD];

    public static final int BROADCAST_RADIUS =
        (int) (RobotType.SCOUT.sensorRadiusSquared * (GameConstants.BROADCAST_RANGE_MULTIPLIER
            + 0.5 / GameConstants.BROADCAST_ADDITIONAL_DELAY_INCREASE));

    public static final int RELAY_RADIUS =
        (int) (RobotType.SCOUT.sensorRadiusSquared * (GameConstants.BROADCAST_RANGE_MULTIPLIER
            + 0.1 / GameConstants.BROADCAST_ADDITIONAL_DELAY_INCREASE));

    public static final int MINIMUM_RADIUS =
        (int) (RobotType.SCOUT.sensorRadiusSquared * GameConstants.BROADCAST_RANGE_MULTIPLIER);

    public static final int ARCHON_RADIUS =
        (int) (RobotType.ARCHON.sensorRadiusSquared * (GameConstants.BROADCAST_RANGE_MULTIPLIER
            + 0.05 / GameConstants.BROADCAST_ADDITIONAL_DELAY_INCREASE));


    public static int lastRound = 0;

    public static boolean[] createSet(MessageType[] types) {
        boolean[] set = new boolean[MessageType.values().length];
        for (int i = types.length - 1; i >= 0; i--) {
            set[types[i].ordinal()] = true;
        }
        return set;
    }

    public static boolean archonBroadcast(Message m) throws GameActionException {
        // broadcast if its been delay since lastRound
        RobotController rc = Controller.crc;
        int[] enc = Messager.encode(m);
        rc.broadcastMessageSignal(enc[0], enc[1], ARCHON_RADIUS);
        return true;
    }

    public static boolean shortBroadcast(Message m, int delay) throws GameActionException {
        // broadcast if its been delay since lastRound
        RobotController rc = Controller.crc;
        int round = rc.getRoundNum();
        if (round >= delay + lastRound) {
            lastRound = round;
            int[] enc = Messager.encode(m);
            rc.broadcastMessageSignal(enc[0], enc[1], RELAY_RADIUS);
            return true;
        }

        return false;
    }

    public static boolean broadcast(Message m, int delay) throws GameActionException {
        // broadcast if its been delay since lastRound
        RobotController rc = Controller.crc;
        int round = rc.getRoundNum();
        if (round >= delay + lastRound) {
            lastRound = round;
            int[] enc = Messager.encode(m);
            rc.broadcastMessageSignal(enc[0], enc[1], BROADCAST_RADIUS);
            return true;
        }

        return false;
    }

    public static void relay(Signal[] sigs, boolean[] messageTypes) throws GameActionException {
        RobotController rc = Controller.crc;
        Team team = rc.getTeam();
        int RELAY_RADIUS = Relayer.RELAY_RADIUS;

        for (int i = Math2.min(sigs.length, Constants.MAX_SIGNALS_IN) - 1; i >= 0; i--) {
            Signal s = sigs[i];

            if (s.getTeam() != team) {
                continue;
            }

            int[] x = s.getMessage();
            MessageType type = Messager.type(x);
            if (!messageTypes[type.ordinal()]) {
                continue;
            }

            int id = Messager.id(x);
            if (seen[id]) {
                continue;
            }

            seen[id] = true;
            rc.broadcastMessageSignal(x[0], x[1], RELAY_RADIUS);
        }
    }
}
