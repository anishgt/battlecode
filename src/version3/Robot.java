package version3;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;

public class Robot {
	
	public static RobotController rc;
	public static Random rnd;
	public static int[] tryDirections = {0,-1,1,-2,2,3,-3,4};
	static Direction initialDirection;
	public static int INFINITY = 10000;
	
	static int targetX = -1;
	static int targetY = -1;
	static int archonX;
	static int archonY;
	static boolean archonFound = false;
	static int MOVE_X = 182632;
	static int MOVE_Y = 1827371;
	static int FOUND_ARCHON_X = 756736;
	static int FOUND_ARCHON_Y = 256253;
	 
	static ArrayList<MapLocation> pastLocations = new ArrayList<>();
	static int STATE_EXPLORE = 10000; 
	static int STATE_DEFEND = 10001;
	static int STATE_ATTACK = 10002;
	static int HUNT_ZOMBIE_DEN = 10003;
	static int gameState = STATE_EXPLORE;
	static int FOUND_NEUTRALBOTS= 756737;
	static int CONVERTED_NEUTRALBOTS = 756738;
	static int CHANGE_STATE = 756739;
	static int FOUND_PARTS = 9843;
	static int COLLECTED_PARTS = 9844;
	static int partX;
	static int partY;
	
	public static void initializeRobot(RobotController rcIn){
		rc=rcIn;
		rnd = new Random(rc.getID());
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
	
	public static Direction randomDirection() {
		return Direction.values()[(int)(rnd.nextDouble()*8)];
	}
	
	public static MapLocation findWeakest(RobotInfo[] listOfRobots){
		double weakestSoFar = -100;
		MapLocation weakestLocation = null;
		for(RobotInfo r:listOfRobots){
			double weakness = r.maxHealth-r.health;
			if(weakness>weakestSoFar){
				weakestLocation = r.location;
				weakestSoFar=weakness;
			}
		}
		return weakestLocation;
	}

	public static MapLocation[] combineThings(RobotInfo[] visibleEnemyArray, Signal[] incomingSignals) {
		ArrayList<MapLocation> attackableEnemyArray = new ArrayList<MapLocation>();
		for(RobotInfo r:visibleEnemyArray){
			attackableEnemyArray.add(r.location);
		}
		for(Signal s:incomingSignals){
			if(s.getTeam()==rc.getTeam().opponent()){
				MapLocation enemySignalLocation = s.getLocation();
				int distanceToSignalingEnemy = rc.getLocation().distanceSquaredTo(enemySignalLocation);
				if(distanceToSignalingEnemy<=rc.getType().attackRadiusSquared){
					attackableEnemyArray.add(enemySignalLocation);
				}
			}
		}
		MapLocation[] finishedArray = new MapLocation[attackableEnemyArray.size()];
		for(int i=0;i<attackableEnemyArray.size();i++){
			finishedArray[i]=attackableEnemyArray.get(i);
		}
		return finishedArray;
	}
	
	public static void sendInstructions() throws GameActionException {
		if (rc.getRoundNum() % 50 == 0) {
			if (!archonFound) {
				MapLocation loc = rc.getLocation();
				rc.broadcastMessageSignal(MOVE_X, loc.x, INFINITY);
				rc.broadcastMessageSignal(MOVE_Y, loc.y, INFINITY);
			} else {
				rc.broadcastMessageSignal(MOVE_X, archonX, INFINITY);
				rc.broadcastMessageSignal(MOVE_Y, archonY, INFINITY);
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
