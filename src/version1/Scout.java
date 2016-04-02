package version1;

import battlecode.common.*;
import java.util.HashSet;

public class Scout extends Robot{
	///CHECK IF UNDER ATTACK
	static int scoutCreationRound = -1;
	static int turnsLeft = 0; // number of turns to move in scoutDirection
	static Direction scoutDirection = null; // random direction
	public static HashSet<MapLocation> knownNeutralLocation = new HashSet<MapLocation>();
	private static void pickNewDirection() throws GameActionException {
		scoutDirection = randomDirection();
		turnsLeft = 100;
	}
	public static void scoutCode() throws GameActionException{
		readInstructions();
		if (gameState == STATE_EXPLORE){
			RobotInfo[] neutralBots = rc.senseNearbyRobots(rc.getLocation(), 35, Team.NEUTRAL);
			if (neutralBots.length > 0){
				for (int i=neutralBots.length-1; i>=0; i--){
					if (!knownNeutralLocation.contains(neutralBots[i].location)){
						knownNeutralLocation.add(neutralBots[i].location);
						rc.broadcastMessageSignal(FOUND_NEUTRALBOTS, (neutralBots[i].location.x*1000 + neutralBots[i].location.y), INFINITY);
						//System.out.println("Scout : Found a neutral bot at: "+neutralBots[i].location);
					}
				}
			}
		}
		if (rc.isCoreReady()) {
			if (turnsLeft == 0) {
				pickNewDirection();
			} else {
				turnsLeft--;
				if (!rc.onTheMap(rc.getLocation().add(scoutDirection))) {
					pickNewDirection();
				}
				tryToMove(scoutDirection);
			}
		}
		
		RobotInfo[] enemies = rc.senseNearbyRobots(rc.getLocation(), INFINITY, rc.getTeam().opponent());
		for (RobotInfo r : enemies) {
			if (r.type == RobotType.ARCHON) {
				if(!(r.location.x == archonX && r.location.y == archonY)){
					int loc = r.location.x*1000 + r.location.y;
					rc.broadcastMessageSignal(FOUND_ARCHON_X, loc, INFINITY);
					break;
				}
			}
		}	
		RobotInfo[] moreEnemies = rc.senseHostileRobots(rc.getLocation(), INFINITY);
		if (moreEnemies.length > 0) {
			Direction away = rc.getLocation().directionTo(enemies[0].location).opposite();
			tryToMove(away);
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
