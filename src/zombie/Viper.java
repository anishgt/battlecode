package zombie;

import battlecode.common.*;

public class Viper extends Robot {

	public static void viperCode() throws GameActionException {
		
		System.out.println("Viper code ");
		if(gameState == ZOMBIE_APOCALYPSE)
		{
			RobotInfo[] nearByRobots = rc.senseNearbyRobots();  
			if (nearByRobots.length > 0) {
				while (rc.isWeaponReady()) {
					MapLocation toAttack = findWeakest(nearByRobots);
					rc.attackLocation(toAttack);
				}
				return;
			}
		}
		if (rc.isCoreReady()) {
			if (assembleX != -1 && assembleY != -1){
				MapLocation target = new MapLocation(assembleX, assembleY);
				Direction dir = rc.getLocation().directionTo(target);
				tryToMove(dir);
			}
			else
			{
				MapLocation[] original = rc.getInitialArchonLocations(rc.getTeam());
				assembleX = original[0].x;
				assembleY = original[0].y;
			}
		}
	}
		

}
