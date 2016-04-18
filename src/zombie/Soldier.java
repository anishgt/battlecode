package zombie;

import battlecode.common.*;

public class Soldier extends Robot{
	
	public static void soldierCode() throws GameActionException {
		
		if(gameState == ZOMBIE_APOCALYPSE && rc.isInfected())
		{
			System.out.println("Going to die ");
			rc.disintegrate();
		}
		else if(gameState == ZOMBIE_APOCALYPSE)
		{
			
			
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
//					assembleX = targetX;
//					assembleY = targetY;
				}
			}
			return;
		}
		
		RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
		int count=0;
		while(nearbyEnemies.length>0 && count<40){
			//System.out.println(rc.getRoundNum());
			
			
			if (rc.isWeaponReady()) {
				MapLocation toAttack = findWeakest(nearbyEnemies);
				rc.attackLocation(toAttack);
			}
			nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
			count++;
			if (count>=30)
				return;
		}
		readInstructions();
		if (rc.isCoreReady()) {
			if (targetX != -1 && targetY != -1){
				MapLocation target = new MapLocation(targetX, targetY);
				Direction dir = rc.getLocation().directionTo(target);
				//tryToMove(dir);
				PathFinding.move(target,dir);
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
			}
		
		}
	}

}
