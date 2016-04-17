package version3;

import battlecode.common.*;
import final_25.FastMath;

import java.util.HashSet;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class Archon extends Robot{
	
	public static int ELECTION_CODE = 23874;
	public static RobotType[] buildList = new RobotType[]{
			RobotType.SCOUT, RobotType.SOLDIER, RobotType.GUARD
	}; 
	public static MapLocation closestNeutral = null;
	public static MapLocation closestParts = null;
	public static boolean isLeader = false;
	public static HashSet<MapLocation> knownNeutralLocation = new HashSet<MapLocation>();
	public static HashSet<MapLocation> knownPartsLocation = new HashSet<MapLocation>();
	public static HashSet<MapLocation> processedNeutralLocation = new HashSet<MapLocation>();
	public static HashSet<MapLocation> collectedPartsLocation = new HashSet<MapLocation>();
	public static void archonCode() throws GameActionException {

		
		if (isLeader){}
			//selctGameState();
		if(gameState==STATE_EXPLORE){
			exploreStateActions();
		}else if (gameState==STATE_ATTACK){
			attackActions();
		}
		
	}
	
	public static void selctGameState() throws GameActionException{
		if(rc.getRobotCount()>25 && targetX!=-1 && targetY!=-1){
			gameState=STATE_ATTACK;
			rc.broadcastMessageSignal(CHANGE_STATE, STATE_ATTACK, INFINITY);
		}
	}
	
	public static void exploreStateActions() throws GameActionException{
		
		if(underAttackActions()==1){
			//System.out.println("Run Away");
			return;
		}
		readInstructions();
		if (isLeader) {
			sendInstructions();
		}
		if(unitProduction()==1){
			//System.out.println("Unit Production");
			return;
		}
		if(partsGatheringActions()==1){
			//System.out.println("Parts");
			return;
		}
		if(resourceGatheringActions()==1){
			//System.out.println("Resource");
			return;
		}
		if(electLeader()==1){
			//System.out.println("Leader");
			return;
		}
		
		
	}
	
	public static void attackStateActions() throws GameActionException{
		
		if(underAttackActions()==1){
			System.out.println("Run Away");
			return;
		}
		readInstructions();
		if(attackActions()==1){
			System.out.println("Attack");
			return;
		}
		if (isLeader) {
			sendInstructions();
		}
		if(unitProduction()==1){
			System.out.println("Unit Production");
			return;
		}
		if(resourceGatheringActions()==1){
			System.out.println("Resource");
			return;
		}
		if(electLeader()==1){
			System.out.println("Leader");
			return;
		}

		
	}
	
	public static int underAttackActions() throws GameActionException {
		RobotInfo[] enemies = null;
		if (rc.isCoreReady()){
			enemies = rc.senseHostileRobots(rc.getLocation(), 16);
			if ((enemies.length == 0)||(enemies.length==1 && enemies[0].type==RobotType.SCOUT))
				return -1;
		}
		while(rc.isCoreReady() && enemies.length>0){
			int i=0;
			for(i=0; i<enemies.length; i++){
				if (enemies[i].type==RobotType.SCOUT)
					continue;
				Direction away = rc.getLocation().directionTo(enemies[i].location).opposite();
				tryToMove(away);
				break;
			}
			
			//return 1;
			enemies = rc.senseHostileRobots(rc.getLocation(), 16);
		}
		return 1;
	}
	
	public static int unitProduction() throws GameActionException {
		if(rc.isCoreReady()){
			Direction randomDir = randomDirection();
			RobotType toBuild;
			if(rc.getRoundNum()<=5 && gameState==STATE_EXPLORE){
				createBuildList(0);
				toBuild = buildList[rnd.nextInt(buildList.length)];
			}else{
				createBuildList(1);
				toBuild = buildList[rnd.nextInt(buildList.length)];
			}
			if(rc.getTeamParts()>=RobotType.GUARD.partCost){
				if(rc.canBuild(randomDir, toBuild)){
					rc.build(randomDir,toBuild);
					return 1;
				}
			}
		}
		return -1;
	}
	
	public static int resourceGatheringActions() throws GameActionException {
		if(rc.isCoreReady()){
			partsGatheringActions();
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
			
			RobotInfo[] adjacentNeutrals = rc.senseNearbyRobots(2, Team.NEUTRAL);
			//System.out.println("Found adjacent neutral"+adjacentNeutrals.length);
			for (RobotInfo neutral : adjacentNeutrals) {
				//System.out.println("Trying to activate");
				rc.activate(neutral.location);
				knownNeutralLocation.remove(neutral.location);
				closestNeutral=null;
				processedNeutralLocation.add(neutral.location);
				rc.broadcastMessageSignal(CONVERTED_NEUTRALBOTS,neutral.location.x*1000 + neutral.location.y,INFINITY); 
				targetX=-1;
				targetY=-1;
				return 1;
			}
			
			if (closestNeutral != null){
				PathFinding.bugPathing(rc.getLocation(), closestNeutral);
				//PathFinding.move(target,dir);
			}
		}
		return -1;
	}
	
	public static int partsGatheringActions() throws GameActionException {
		if(rc.isCoreReady()){
			MapLocation[] partLocs = rc.sensePartLocations(RobotType.ARCHON.sensorRadiusSquared);
			if (partLocs.length > 0){
				for (int i=partLocs.length-1; i>=0; i--){
					if (!knownPartsLocation.contains(partLocs[i])){
						knownPartsLocation.add(partLocs[i]);
						//System.out.println("Archon : Found a neutral bot at: "+neutralBots[i].location);
					}
				}
			}
			
			MapLocation bestPartLoc = null;
			if (partLocs.length > 0) {
				double bestScore = Double.NEGATIVE_INFINITY;
				for (MapLocation partLoc : knownPartsLocation) {
					double numParts = rc.senseParts(partLoc);
					double score = numParts - 20 * Math.sqrt(rc.getLocation().distanceSquaredTo(partLoc));
					/*if (rc.senseRobotAtLocation(partLoc) != null) {
						score -= 100;
					}*/
					if (score > bestScore) {
						bestScore = score;
						bestPartLoc = partLoc;
					}
				}	
				closestParts = bestPartLoc;
			}
			
			if (bestPartLoc != null){
				PathFinding.bugPathing(rc.getLocation(), closestParts);
				//PathFinding.move(target,dir);
				return 1;
			}
		}
		return -1;
	}
	
	public static int attackActions() throws GameActionException {
		if(rc.isCoreReady()){
			MapLocation target = new MapLocation(targetX, targetY);
			if(targetX!=-1 && targetY!=-1){
				//PathFinding.move(target,dir);
				//System.out.println("Moving towards target" + targetX + targetY);
				PathFinding.bugPathing(rc.getLocation(), target);
				//PathFinding.move(target,dir);
				
				//PathFinding.bugPathing(rc.getLocation(), target);
				return 1;
			}
		}
		return -1;
	}
	
	public static void collectParts() throws GameActionException {
		if(rc.isCoreReady()){
			MapLocation[] partLocs = rc.sensePartLocations(RobotType.ARCHON.sensorRadiusSquared);
			MapLocation bestPartLoc = null;
			if (partLocs.length > 0) {
				double bestScore = Double.NEGATIVE_INFINITY;
				for (MapLocation partLoc : partLocs) {
					double numParts = rc.senseParts(partLoc);
					double score = numParts - 20 * Math.sqrt(rc.getLocation().distanceSquaredTo(partLoc));
					if (rc.senseRobotAtLocation(partLoc) != null) {
						score -= 100;
					}
					if (score > bestScore) {
						bestScore = score;
						bestPartLoc = partLoc;
					}
				}	
			}	
			PathFinding.bugPathing(rc.getLocation(), bestPartLoc);
		}
	}
	
	public static int electLeader() throws GameActionException {
		if (rc.getRoundNum() % 300 == 0 && rc.getType() == RobotType.ARCHON) {
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
				return 1;
			} else {
				isLeader = false;
				return 1;
			}
		}
		return -1;
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
				if (rc.getRobotCount() > 15){
					int loc = s.getMessage()[1];
					archonX = loc /1000;
					archonY = loc % 1000;
					targetX = archonX;
					targetY = archonY;
					archonFound = true;
				}
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
				
			}else if (command == FOUND_PARTS){
				int loc = s.getMessage()[1];
				MapLocation location = new MapLocation(loc /1000,loc % 1000);
				if(!collectedPartsLocation.contains(location)){
					knownPartsLocation.add(location);
					System.out.println("Archon rec");
				}
			}else if (command == COLLECTED_PARTS){
				int loc = s.getMessage()[1];
				MapLocation location = new MapLocation(loc /1000,loc % 1000);
				if(!collectedPartsLocation.contains(location)){
					knownPartsLocation.add(location);
				}
			}
		}
	}
	
	private static void createBuildList(int stage){
		if (stage==0)
			buildList = new RobotType[]{RobotType.SCOUT};
		else if (stage == 1)
			buildList = new RobotType[]{RobotType.SCOUT,RobotType.SOLDIER,RobotType.SOLDIER,RobotType.SOLDIER,RobotType.SOLDIER,RobotType.SOLDIER,RobotType.SOLDIER,RobotType.SOLDIER,RobotType.SOLDIER};
		else if (stage == 2)
			buildList = new RobotType[]{RobotType.SCOUT,RobotType.GUARD,RobotType.GUARD,RobotType.GUARD,RobotType.GUARD,RobotType.SOLDIER,RobotType.SOLDIER,RobotType.SOLDIER,RobotType.SOLDIER};
		else if (stage == 3)
			buildList = new RobotType[]{RobotType.SCOUT, RobotType.GUARD, RobotType.SOLDIER,RobotType.GUARD, RobotType.SOLDIER,RobotType.GUARD, RobotType.SOLDIER, RobotType.GUARD, RobotType.SOLDIER, RobotType.GUARD, RobotType.SOLDIER, RobotType.GUARD, RobotType.SOLDIER};
	}


}
