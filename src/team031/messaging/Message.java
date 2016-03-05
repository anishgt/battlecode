package team031.messaging;

import battlecode.common.MapLocation;

/**
 * Created by jdshen on 1/5/16.
 */
public class Message {
    public final static int MAX_LOCS = 3;

    public Message() {

    }

    public Message(MessageType type, int id) {
        this.type = type;
        this.id = id;
    }

    public Message(MessageType type, int id, MapLocation[] locs) {
        this.type = type;
        this.id = id;
        this.locs = locs;
    }

    public Message(MessageType type, MapLocation[] locs) {
        this.type = type;
        this.locs = locs;
    }

    public MessageType type;
    public int id;
    public int group;

    // number of locs
    public MapLocation[] locs;
}
