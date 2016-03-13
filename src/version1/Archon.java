package version1;

import battlecode.common.*;

public class Archon extends Robot{
	
	public static int ELECTION_CODE = 23874;
	public static RobotType[] buildList = new RobotType[]{
			RobotType.SCOUT, RobotType.SOLDIER, RobotType.GUARD, RobotType.TURRET
	};
	public static boolean isLeader = false;
	
	public static void archonCode() throws GameActionException {
		electLeader();
		readInstructions();
		if (isLeader) {
			sendInstructions();
		}
		if(rc.isCoreReady()){
			Direction randomDir = randomDirection();
			RobotType toBuild = buildList[rnd.nextInt(buildList.length)];
			if(rc.getTeamParts()>=RobotType.SCOUT.partCost){
				if(rc.canBuild(randomDir, toBuild)){
					rc.build(randomDir,toBuild);
					return;
				}
			}
			MapLocation target = new MapLocation(targetX, targetY);
			Direction dir = rc.getLocation().directionTo(target);
			
			RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), INFINITY);
			if (enemies.length > 0) {
				Direction away = rc.getLocation().directionTo(enemies[0].location).opposite();
				tryToMove(away);
			} else {
				tryToMove(dir);
			}
		}
		
	}
	
	public static void electLeader() throws GameActionException {
		if (rc.getRoundNum() % 100 == 0 && rc.getType() == RobotType.ARCHON) {
			rc.broadcastMessageSignal(ELECTION_CODE, 0, INFINITY);
			Signal[] receivedSignal = rc.emptySignalQueue();
			int numArchons = 0;
			for (Signal s : receivedSignal) {
				if (s.getMessage() != null && s.getMessage()[0] == ELECTION_CODE) {
					numArchons++;
				}
			}
			if (numArchons == 0) {
				// If you haven't received anything yet, then you're the leader.
				isLeader = true;
			} else {
				isLeader = false;
			}
		}
		
		
	}
	
	private static void createBuildList(int stage){
		if (stage==0)
			buildList = new RobotType[]{RobotType.SCOUT};
		else if (stage == 1)
			buildList = new RobotType[]{RobotType.GUARD};
		else if (stage == 2)
			buildList = new RobotType[]{RobotType.GUARD, RobotType.SOLDIER};
	}


}
