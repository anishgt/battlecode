package version1;

import battlecode.common.*;

public class Soldier extends Robot{
	
	public static void soldierCode() throws GameActionException {
		readInstructions();
		
		RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
		if (nearbyEnemies.length > 0) {
			if (rc.isWeaponReady()) {
				MapLocation toAttack = findWeakest(nearbyEnemies);
				rc.attackLocation(toAttack);
			}
			return;
		}
		
		if (rc.isCoreReady()) {
			MapLocation target = new MapLocation(targetX, targetY);
			Direction dir = rc.getLocation().directionTo(target);
			tryToMove(dir);
		}
	}


}
