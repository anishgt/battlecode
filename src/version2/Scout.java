package version2;

import battlecode.common.*;
import java.util.HashSet;

public class Scout extends Robot{
	
	static int scoutCreationRound = -1;
	static int turnsLeft = 0; // number of turns to move in scoutDirection
	static Direction scoutDirection = null; // random direction
	private static void pickNewDirection() throws GameActionException {
		scoutDirection = randomDirection();
		turnsLeft = 100;
	}
	static int reportObstaclesToArchon=10;
	static int obsindex=0;
	static MapLocation[] obstaclesFound= new MapLocation[reportObstaclesToArchon];
	static HashSet<MapLocation> locationssearched = new HashSet<MapLocation>();
	
	public static int scanRubbles(int msgCount) throws GameActionException{
		if(rc.isCoreReady()){
			MapLocation[] toExplore = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 53);
			for (MapLocation iter:toExplore){
				
				double rubble = rc.senseRubble(iter);
				if (rubble >= GameConstants.RUBBLE_OBSTRUCTION_THRESH){
					rc.broadcastMessageSignal(OBSTACLE_BLOCKED, (iter.x*1000 + iter.y), 1000);
					msgCount++;
					if (msgCount >=20)
						return msgCount;
				} 
			}
			
		}
		return msgCount;
	}
	public static void tryToMove(Direction forward) throws GameActionException{
		if(rc.isCoreReady()){
			for(int deltaD:tryDirections){
				Direction newDirection = Direction.values()[(forward.ordinal()+deltaD+8)%8];
				MapLocation newLocation = rc.getLocation().add(newDirection);
				if(rc.canMove(newDirection) && !pastLocations.contains(newLocation)){
					pastLocations.add(rc.getLocation());
					if(pastLocations.size()>20){
						pastLocations.remove(0);
					}
					rc.move(newDirection);
					return;
				}				
			}
			if(rc.getType().canClearRubble()){
				//failed to move, look to clear rubble
				MapLocation ahead = rc.getLocation().add(forward);
				if(rc.senseRubble(ahead)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
					rc.clearRubble(forward);
				}
			}
		}
	}
	
	public static void scoutCode() throws GameActionException{
		int msgCount=0;
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
		if (turnsLeft%50 ==0)
			msgCount = scanRubbles(msgCount);	
	}

}
