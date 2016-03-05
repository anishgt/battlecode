package team031.actors;

import battlecode.common.GameActionException;

/**
 * Created by jdshen on 1/6/16.
 */
public interface Actor {
    boolean act(boolean move, boolean attack) throws GameActionException;
}
