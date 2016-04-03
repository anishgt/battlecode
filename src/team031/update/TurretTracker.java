package team031.update;

import battlecode.common.MapLocation;
import battlecode.common.Signal;
import team031.controllers.Controller;
import team031.messaging.Message;
import team031.messaging.MessageType;
import team031.messaging.Messager;
import team031.util.Constants;
import team031.util.FastIterableLocSet;
import team031.util.FastLocMap;

public class TurretTracker {
    // number of turrets is limited by number of signals we read in
    private static final MapLocation[][] past
        = new MapLocation[Constants.TURRET_STATIONARY_TURNS][Constants.MAX_TURRET_TRACK];
    private static final int[] size = new int[Constants.TURRET_STATIONARY_TURNS];

    public static final FastIterableLocSet turrets = new FastIterableLocSet();
    public static final FastLocMap turretToRound = new FastLocMap();

    // should be called AFTER signals
    public static void update() {
        int round = Controller.crc.getRoundNum();
        int index = round % Constants.TURRET_STATIONARY_TURNS;

        // garbage collect turrets that havent been updated again
        MapLocation[] last = past[index];
        int lastRound = index - past.length;
        for (int i = size[index] - 1; i >= 0; i--) {
            if (turretToRound.get(last[i]) <= lastRound) {
                turrets.remove(last[i]);
            }
        }

        past[index] = new MapLocation[Constants.MAX_TURRET_TRACK];
        size[index] = 0;

        // add all turrets from messages this round
        last = past[index];
        int turretType = MessageType.TURRETS_AT.ordinal();
        Signal[] bucket = Signals.buckets[turretType];

        for (int i = Signals.size[turretType] - 1; i >= 0; i--) {
            int[] enc = bucket[i].getMessage();
            Message m = Messager.decode(enc[0], enc[1]);

            // update round count, the turret set, and history
            for (int j = m.locs.length - 1; j >= 0; j--) {
                MapLocation loc = m.locs[j];
                turretToRound.add(loc, round);
                turrets.add(loc);
                last[size[index]++] = loc;
            }
        }
    }


    public static MapLocation[] getTurrets() {
        return turrets.getKeys();
    }
}
