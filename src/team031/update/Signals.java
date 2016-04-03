package team031.update;

import battlecode.common.Signal;
import battlecode.common.Team;
import team031.controllers.Controller;
import team031.messaging.MessageType;
import team031.messaging.Messager;
import team031.util.Constants;
import team031.util.Math2;

/**
 * Created by jdshen on 1/7/16.
 */
public class Signals {
    public static Signal[] sigs = new Signal[0];
    public static Signal[][] buckets = new Signal[MessageType.values().length][Constants.MAX_SIGNALS_IN];
    public static int[] size = new int[MessageType.values().length];

    public static void update() {
        sigs = Controller.crc.emptySignalQueue();

        size = new int[MessageType.values().length];
        buckets = new Signal[MessageType.values().length][Constants.MAX_SIGNALS_IN];
        Team team = Controller.c.team;

        for (int i = Math2.min(sigs.length, Constants.MAX_SIGNALS_IN) - 1; i >= 0; i--) {
            if (sigs[i].getTeam() != team) {
                continue;
            }

            int type = Messager.type(sigs[i].getMessage()).ordinal();
            buckets[type][size[type]++] = sigs[i];
        }
    }

    public static Signal readOnce() {
        Signal[] sigs = Controller.crc.emptySignalQueue();
        if (sigs.length > 0) {
            return sigs[0];
        } else {
            return null;
        }
    }

    // TODO make a broadcast queue, so you broadcast after moving.
}
