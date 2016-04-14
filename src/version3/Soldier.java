package version3;

import battlecode.common.*;

public class Soldier extends Robot{
	
	public static void soldierCode() throws GameActionException {
		RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
		int count=0;
		while(nearbyEnemies.length>0 && count<60){
			System.out.println(rc.getRoundNum());
			
			
			if (rc.isWeaponReady()) {
				MapLocation toAttack = findWeakest(nearbyEnemies);
				rc.attackLocation(toAttack);
			}
			nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
			count++;
			if (count>=50)
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
