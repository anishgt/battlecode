package version1Pathfinding;

import battlecode.common.*;
import java.util.HashSet;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class Archon extends Robot{
	
	public static int ELECTION_CODE = 23874;
	public static RobotType[] buildList = new RobotType[]{
			RobotType.SCOUT, RobotType.SOLDIER, RobotType.GUARD
	}; 
	public static MapLocation closestNeutral = null;
	public static boolean isLeader = false;
	public static HashSet<MapLocation> knownNeutralLocation = new HashSet<MapLocation>();
	public static HashSet<MapLocation> processedNeutralLocation = new HashSet<MapLocation>();
	public static void archonCode() throws GameActionException {
		electLeader();
		readInstructions();
		if (isLeader) {
			sendInstructions();
		}
		/*if (gameState == STATE_EXPLORE){
			
			RobotInfo[] neutralBots = rc.senseNearbyRobots(rc.getLocation(), 35, Team.NEUTRAL);
			if (neutralBots.length > 0){
				for (int i=neutralBots.length-1; i>=0; i--){
					if (!knownNeutralLocation.contains(neutralBots[i].location) && !processedNeutralLocation.contains(neutralBots[i].location)){
						knownNeutralLocation.add(neutralBots[i].location);
						//System.out.println("Archon : Found a neutral bot at: "+neutralBots[i].location);
					}
				}
			}
			if (targetX==-1 && targetY==-1){
				MapLocation closest=new MapLocation(INFINITY, INFINITY);
				int closestdist= INFINITY;
				for (MapLocation iter: knownNeutralLocation){
					int dist = VectorMath.sqnorm(VectorMath.sub(rc.getLocation(), iter));
					if (dist < closestdist){
						dist = closestdist;
						closest = iter;
					}
					
				}
				//System.out.println("Archon setting target");
				closestNeutral = closest;
			}
			
		}
		if(rc.isCoreReady()){
			
			Direction randomDir = randomDirection();
			RobotType toBuild;
			if(rc.getRobotCount()<=5 && gameState==STATE_EXPLORE){
				createBuildList(0);
				toBuild = buildList[rnd.nextInt(buildList.length)];
			}else{
				createBuildList(1);
				toBuild = buildList[rnd.nextInt(buildList.length)];
			}
			if(rc.getTeamParts()>=RobotType.SCOUT.partCost){
				if(rc.canBuild(randomDir, toBuild)){
					rc.build(randomDir,toBuild);
					return;
				}
			}*/
			MapLocation target = new MapLocation(targetX, targetY);
			Direction dir = rc.getLocation().directionTo(target);

			/*RobotInfo[] enemies = rc.senseHostileRobots(rc.getLocation(), INFINITY);
			if (enemies.length > 0) {
				Direction away = rc.getLocation().directionTo(enemies[0].location).opposite();
				tryToMove(away);
			} */
			/*if(rc.isCoreReady()){
				RobotInfo[] adjacentNeutrals = rc.senseNearbyRobots(2, Team.NEUTRAL);
				//System.out.println("Found adjacent neutral"+adjacentNeutrals.length);
				for (RobotInfo neutral : adjacentNeutrals) {
					//System.out.println("Trying to activate");
					rc.activate(neutral.location);
					knownNeutralLocation.remove(neutral.location);
					closestNeutral=null;
					processedNeutralLocation.add(neutral.location);
					rc.broadcastMessageSignal(CONVERTED_NEUTRALBOTS,neutral.location.x*1000 + neutral.location.y,INFINITY); 
					
				}
			}
			if (closestNeutral != null){
				PathFinding.bugPathing(rc.getLocation(), closestNeutral);
				//PathFinding.move(target,dir);
			}*/
			System.out.println("check"+targetX+targetY);
			if(!(targetX==-1 && targetY==-1)){
				System.out.println("check"+targetX+targetY);
				//PathFinding.move(target,dir);
				//System.out.println("Moving towards target" + targetX + targetY);
				//PathFinding.bugPathing(rc.getLocation(), target);
				//PathFinding.move(target,dir);
				tryToMove(dir);
				//PathFinding.bugPathing(rc.getLocation(), target);
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
					int loc = s.getMessage()[1];
					archonX = loc /1000;
					archonY = loc % 1000;
					targetX = archonX;
					targetY = archonY;
					archonFound = true;
			} else if (command == FOUND_NEUTRALBOTS){
				int loc = s.getMessage()[1];
				MapLocation location = new MapLocation(loc /1000,loc % 1000);
				if(!processedNeutralLocation.contains(location)){
					knownNeutralLocation.add(location);
					//System.out.println("Archon : Received from scout. Found a neutral bot at: "+location);
				}
				
			} else if (command == CONVERTED_NEUTRALBOTS){
				int loc = s.getMessage()[1];
				MapLocation location = new MapLocation(loc /1000,loc % 1000);
				if(knownNeutralLocation.contains(location)){
					knownNeutralLocation.remove(location);
					//System.out.println("Archon : Received from scout. Found a neutral bot at: "+location);
				}
				
			}
		}
	}
	
	private static void createBuildList(int stage){
		if (stage==0)
			buildList = new RobotType[]{RobotType.SCOUT};
		else if (stage == 1)
			buildList = new RobotType[]{RobotType.GUARD,RobotType.GUARD,RobotType.GUARD,RobotType.GUARD,RobotType.GUARD,RobotType.GUARD,RobotType.SOLDIER,RobotType.SOLDIER};
		else if (stage == 2)
			buildList = new RobotType[]{RobotType.SCOUT, RobotType.GUARD, RobotType.SOLDIER,RobotType.GUARD, RobotType.SOLDIER,RobotType.GUARD, RobotType.SOLDIER, RobotType.GUARD, RobotType.SOLDIER, RobotType.GUARD, RobotType.SOLDIER, RobotType.GUARD, RobotType.SOLDIER};
	}


}
