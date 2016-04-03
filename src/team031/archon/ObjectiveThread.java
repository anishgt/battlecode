package team031.archon;

import battlecode.common.MapLocation;
import team031.controllers.Controller;
import team031.messaging.Message;
import team031.messaging.MessageType;

/**
 * Created by jdshen on 1/22/16.
 */
public class ObjectiveThread {
    public MessageType type;
    public MapLocation loc;
    public int hash;
    public boolean control;
    public int round;

    public ObjectiveThread(MessageType type, MapLocation loc) {
        this.type = type;
        this.loc = loc;
        this.hash = -1;
        this.control = false;
    }

    public void take(Message m) {
        type = m.type;
        loc = m.locs[0];
        control = true;

        hash = m.id;
        round = Controller.crc.getRoundNum();
    }

    public void reset(Message m) {
        type = m.type;
        loc = m.locs[0];
        if (hash != m.id) {
            control = false;
        }

        hash = m.id;
        round = Controller.crc.getRoundNum();
    }
}
