package team031.util;

import battlecode.common.GameConstants;
import battlecode.common.RobotType;

/**
 * Created by jdshen on 1/5/16.
 */
public class Constants {
    // maximum number of enemies you should check
    public static final int MAX_ENEMIES = 16;

    // when you have this many allies as sensed by a scout, attack!
    public static final int LOTS_OF_ALLIES = 25;

    // number of rounds an attack relay should last for.
    public static final int ATTACK_ROUNDS = 50;

    // max number of decision relays you should look through
    public static final int MAX_DECISIONS = 7;

    // maximum number of allies you should check
    public static final int MAX_ALLIES = 16;

    // largest absolute value of the offset of the map
    public static final int MAX_MAP_OFFSET = 16000;

    // maximum number of signals to look through on a given turn
    public static final int MAX_SIGNALS_OUT = 90;
    public static final int MAX_SIGNALS_IN = 35;

    public static final int ADJACENT_SQ = 2;
    public static final int SEMI_ADJACENT_SQ = 8;

    // used for signalling
    public static final int LOTS_OF_ENEMIES = 7;

    // number of rounds between broadcasts
    public static final int BROADCAST_DELAY = 15;

    // how long a turret is likely to stay in the same position.
    public static final int TURRET_STATIONARY_TURNS = GameConstants.TURRET_TRANSFORM_DELAY * 5;

    public static final int MAX_TURRET_TRACK = 40;

    public static final int AUGMENTED_TURRET_RANGE = 65; // may need to make bigger

    // used for when to avoid enemies. NOTE: must be in attacker look ups
    public static final int SCOUT_ARS = RobotType.ARCHON.attackRadiusSquared;

    public static final int MAX_DISTANCE_SQ = 20000;

    // how many rounds is considered many.
    public static final int ROUND_RESET = 100;
}
