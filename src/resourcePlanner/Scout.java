package resourcePlanner;

import battlecode.common.*;
import java.util.HashSet;

public class Scout extends Robot{
	///CHECK IF UNDER ATTACK
	static int scoutCreationRound = -1;
	static int turnsLeft = 0; // number of turns to move in scoutDirection
	static Direction scoutDirection = null; // random direction
	public static HashSet<MapLocation> knownNeutralLocation = new HashSet<MapLocation>();
	public static HashSet<MapLocation> knownPartsLocation = new HashSet<MapLocation>();
	private static void pickNewDirection() throws GameActionException {
		scoutDirection = randomDirection();
		turnsLeft = 100;
	}
	public static void scoutCode() throws GameActionException{
		readInstructions();
		if(underAttackActions()==1){
			return;
		}
		if(broadcastEnemy()==1){
			return;
		};
		if(resourceGatheringActions()==1){
			return;
		}
		wander();
		
	}
	
	public static int wander() throws GameActionException {
		if (rc.isCoreReady()) {
			if (turnsLeft == 0) {
				pickNewDirection();
			} else {
				turnsLeft--;
				if (!rc.onTheMap(rc.getLocation().add(scoutDirection))) {
					pickNewDirection();
				}
				tryToMove(scoutDirection);
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
		}
	}
	
	public static int resourceGatheringActions() throws GameActionException {
		if (gameState == STATE_EXPLORE){
			RobotInfo[] neutralBots = rc.senseNearbyRobots(rc.getLocation(), 35, Team.NEUTRAL);
			if (neutralBots.length > 0){
				for (int i=neutralBots.length-1; i>=0; i--){
					if (!knownNeutralLocation.contains(neutralBots[i].location)){
						knownNeutralLocation.add(neutralBots[i].location);
						rc.broadcastMessageSignal(FOUND_NEUTRALBOTS, (neutralBots[i].location.x*1000 + neutralBots[i].location.y), INFINITY);
						//System.out.println("Scout : Found a neutral bot at: "+neutralBots[i].location);
						return 1;
					}
				}
			}
		}	
		MapLocation[] partLocs = rc.sensePartLocations(RobotType.ARCHON.sensorRadiusSquared);
		if (partLocs.length > 0){
			for (int i=partLocs.length-1; i>=0; i--){
				if (!knownPartsLocation.contains(partLocs[i])){
					knownPartsLocation.add(partLocs[i]);
					rc.broadcastMessageSignal(FOUND_PARTS, (partLocs[i].x*1000 + partLocs[i].y), INFINITY);
					//System.out.println("Scount broadcasted");
					return 1;
				}
			}
		}
		return -1;
	}
	
	public static int broadcastEnemy() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(rc.getLocation(), INFINITY, rc.getTeam().opponent());
		for (RobotInfo r : enemies) {
			if (r.type == RobotType.ARCHON) {
				if(!(r.location.x == archonX && r.location.y == archonY)){
					int loc = r.location.x*1000 + r.location.y;
					rc.broadcastMessageSignal(FOUND_ARCHON_X, loc, INFINITY);
					return 1;
				}
			}
		}
		return -1;
	}
	
	public static int underAttackActions() throws GameActionException {
		RobotInfo[] enemies = null;
		if (rc.isCoreReady()){
			enemies = rc.senseHostileRobots(rc.getLocation(), 16);
			if (enemies.length == 0)
				return -1;
		}
		while(rc.isCoreReady() && enemies.length>0){
			
			Direction away = rc.getLocation().directionTo(enemies[0].location).opposite();
			tryToMove(away);
			//return 1;
			enemies = rc.senseHostileRobots(rc.getLocation(), 16);
		}
		return 1;
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
			if (command == FOUND_ARCHON_X) {
				if (rc.getRobotCount() > 15){
					int loc = s.getMessage()[1];
					archonX = loc /1000;
					archonY = loc % 1000;
					targetX = archonX;
					targetY = archonY;
					archonFound = true;
				}
			}
		}
	}

}
