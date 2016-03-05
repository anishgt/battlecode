package team031.messaging;

/**
 * Created by jdshen on 1/5/16.
 */
public enum MessageType {
    // tell robot to become type
    ACTOR(Type.ID),

    // a scout message about enemies
    ENEMIES_AT(Type.LOCS), TURRETS_AT(Type.LOCS),

    // archon message about enemies
    HELP(Type.LOCS),

    // notification relays: zombie den, lots of hostiles, or archon
    ATTACK_RELAY(Type.ID_AND_LOCS), ZOMBIE_DEN_RELAY(Type.ID_AND_LOCS),
    ZOMBIES_RELAY(Type.ID_AND_LOCS), OPP_RELAY(Type.ID_AND_LOCS),
    ARCHON_RELAY(Type.ID_AND_LOCS), TURRETS_RELAY(Type.ID_AND_LOCS),
    NEUTRAL_RELAY(Type.ID_AND_LOCS), PARTS_RELAY(Type.ID_AND_LOCS),

    // attack relays, in order of precedence
    ATTACK_DEN(Type.ID_GROUP_LOCS), HELP_ARCHON(Type.ID_GROUP_LOCS), ATTACK_ZOMBIES(Type.ID_GROUP_LOCS),
    ATTACK_OPP(Type.ID_GROUP_LOCS), IDLE(Type.ID_GROUP_LOCS),

    ;

    public final Type type;

    MessageType() {
        this(Type.ID);
    }

    MessageType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    // what kind of message type
    public enum Type {
        ID, LOCS, ID_AND_LOCS, ID_GROUP_LOCS,
    }
}
