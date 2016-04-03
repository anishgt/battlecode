package team031.update;

import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team031.messaging.MessageType;
import team031.util.Constants;
import team031.util.Math2;

public class HostileBuckets {
    public static RobotInfo[][] buckets = new RobotInfo[RobotType.values().length][Constants.MAX_ENEMIES];
    public static int[] size = new int[RobotType.values().length];

    public static void update() {
        size = new int[MessageType.values().length];
        buckets = new RobotInfo[MessageType.values().length][Constants.MAX_SIGNALS_IN];

        RobotInfo[] hostile = Sensor.hostile;
        for (int i = Math2.min(hostile.length, Constants.MAX_ENEMIES) - 1; i >= 0; i--) {
            int type = hostile[i].type.ordinal();
            buckets[type][size[type]++] = hostile[i];
        }
    }
}
