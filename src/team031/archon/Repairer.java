package team031.archon;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team031.controllers.Controller;
import team031.util.Math2;

public class Repairer {
    public int count = 0;

    public void repair() throws GameActionException {
        RobotController rc = Controller.crc;
        Controller c = Controller.c;
        RobotInfo[] allies = rc.senseNearbyRobots(c.ars, c.team);

        int k = -1;
        boolean infected = false;
        double minHealth = 10000;
        // MAGIC CONSTANT
        int max = 10;
        for (int i = Math2.min(allies.length, max) - 1; i >= 0; i--) {
            RobotInfo info = allies[i];
            if (info.type == RobotType.ARCHON) {
                continue;
            }

            if (infected && !(info.zombieInfectedTurns >= 1 || info.viperInfectedTurns >= 1)) {
                continue;
            }

            if (info.health < info.type.maxHealth && minHealth >= info.health) {
                minHealth = info.health;
                infected = info.zombieInfectedTurns >= 1 || info.viperInfectedTurns >= 1;
                k = i;
            }
        }

        if (k >= 0) {
            rc.repair(allies[k].location);
            count++;
        }
    }
}
