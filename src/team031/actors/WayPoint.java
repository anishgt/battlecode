package team031.actors;

import battlecode.common.MapLocation;
import team031.messaging.MessageType;

/**
 * Created by jdshen on 1/22/16.
 */
public class WayPoint {
    public MapLocation to;
    public MessageType type;

    public WayPoint(MapLocation to, MessageType type) {
        this.to = to;
        this.type = type;
    }
}
