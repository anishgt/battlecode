package version1;

import battlecode.common.*;

public class Scout extends Robot{
	
	static int scoutCreationRound = -1;
	static int turnsLeft = 0; // number of turns to move in scoutDirection
	static Direction scoutDirection = null; // random direction
	
	private static void pickNewDirection() throws GameActionException {
		scoutDirection = randomDirection();
		turnsLeft = 100;
	}
	
	public static void scoutCode() throws GameActionException{
		readInstructions();
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
