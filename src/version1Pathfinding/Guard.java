package version1Pathfinding;

import battlecode.common.*;

public class Guard extends Robot{

	public static void guardCode() throws GameActionException {
		/*RobotInfo[] enemyArray = rc.senseHostileRobots(rc.getLocation(), 1000000);
		
		if(enemyArray.length>0){
			if(rc.isWeaponReady()){
				//look for adjacent enemies to attack
				for(RobotInfo oneEnemy:enemyArray){
					if(rc.canAttackLocation(oneEnemy.location)){
						rc.setIndicatorString(0,"trying to attack");
						rc.attackLocation(oneEnemy.location);
						break;
					}
				}
			}
			//could not find any enemies adjacent to attack
			//try to move toward them
			if(rc.isCoreReady()){
				MapLocation goal = enemyArray[0].location;
				Direction toEnemy = rc.getLocation().directionTo(goal);
				tryToMove(toEnemy);
			}
		}else{//there are no enemies nearby
			//check to see if we are in the way of friends
			//we are obstructing them
			if(rc.isCoreReady()){
				RobotInfo[] nearbyFriends = rc.senseNearbyRobots(2, rc.getTeam());
				if(nearbyFriends.length>3){
					Direction away = randomDirection();
					tryToMove(away);
				}else{//maybe a friend is in need!
					RobotInfo[] alliesToHelp = rc.senseNearbyRobots(1000000,rc.getTeam());
					MapLocation weakestOne = findWeakest(alliesToHelp);
					if(weakestOne!=null){//found a friend most in need
						Direction towardFriend = rc.getLocation().directionTo(weakestOne);
						tryToMove(towardFriend);
					}
				}
			}
		}*/
		
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
