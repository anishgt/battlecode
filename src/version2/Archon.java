package version2;

import battlecode.common.*;

public class Archon extends Robot{
	
	public static int ELECTION_CODE = 23874;
	public static RobotType[] buildList = new RobotType[]{
			RobotType.SCOUT, RobotType.SOLDIER, RobotType.GUARD
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
				//RealTimeAStar.move(target);
			} else {
				//tryToMove(dir);
				PathFinding.move(target,dir);
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
	public static void readInstructions() throws GameActionException {
		Signal[] signals = rc.emptySignalQueue();
		for (Signal s : signals) {
			if (s.getTeam() != rc.getTeam()) {
				continue;
			}
			
			if (s.getMessage() == null) {
				continue;
			}
			
			int command = s.getMessage()[0];
			if (command == MOVE_X) {
				targetX = s.getMessage()[1];
			} else if (command == MOVE_Y) {
				targetY = s.getMessage()[1];
			} else if (command == FOUND_ARCHON_X) {
				archonX = s.getMessage()[1];
			} else if (command == FOUND_ARCHON_Y) {
				archonY = s.getMessage()[1];
				denFound = true;
			} else if (command == OBSTACLE_BLOCKED){
				MapLocation obs = new MapLocation(s.getMessage()[1]/100, s.getMessage()[1]%100);
				if (!obstacles.contains(obs)){
					obstacles.add(obs);
					if (isLeader){
						System.out.println("Leader received BLOCKED: "+s.getMessage()[1]);
					}
				}
			} else if (command == OBSTACLE_PARTIAL){
				MapLocation obs = new MapLocation(s.getMessage()[1]/100, s.getMessage()[1]%100);
				if (!obstacles.contains(obs)){
					obstacles.add(obs);
					if (isLeader){
						System.out.println("Leader received PARTIAL: "+s.getMessage()[1]);
					}
				}
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
