package team031.update;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import team031.controllers.Controller;
import team031.nav.Mover;

/**
 * Created by jdshen on 1/9/16.
 */
public class Sensor {
    public static RobotInfo[] hostile = new RobotInfo[0];
    public static boolean[] canMove = new boolean[Direction.values().length];
    public static RobotInfo[] hostileARS = new RobotInfo[0];

    public static void update() {
        RobotController rc = Controller.crc;
        hostile = rc.senseHostileRobots(rc.getLocation(), Controller.c.srs);
        hostileARS = rc.senseHostileRobots(rc.getLocation(), Controller.c.ars);
        canMove = Mover.calcCanMove();
    }
}
