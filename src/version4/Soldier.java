package version4;

import battlecode.common.*;

public class Soldier extends Robot{
	
	public static void soldierCode() throws GameActionException {
		
		RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
		if (nearbyEnemies.length > 0) {
			if (rc.isWeaponReady()) {
				MapLocation toAttack = findWeakest(nearbyEnemies);
				rc.attackLocation(toAttack);
			}
			return;
		}
		readInstructions();
		if (rc.isCoreReady()) {
			if (targetX != -1 && targetY != -1){
				MapLocation target = new MapLocation(targetX, targetY);
				Direction dir = rc.getLocation().directionTo(target);
				tryToMove(dir);
			}
		}
	}


}
