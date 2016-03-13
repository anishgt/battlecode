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
				rc.broadcastMessageSignal(FOUND_ARCHON_X, r.location.x, INFINITY);
				rc.broadcastMessageSignal(FOUND_ARCHON_Y, r.location.y, INFINITY);
				break;
			}
		}	
	}

}
