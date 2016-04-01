package version2;

import java.util.ArrayList;

import battlecode.common.*;

public class PathFinding extends Robot {
	
	static ArrayList<MapLocation> pastLocations = new ArrayList<>();

	public static void move(MapLocation target, Direction forward) throws GameActionException{
		if(rc.isCoreReady()){
			Direction nextDirection = computeRTAStar(target);;
			MapLocation nextLocation = rc.getLocation().add(nextDirection);
			if(rc.canMove(nextDirection) && !pastLocations.contains(nextLocation)){
				pastLocations.add(rc.getLocation());
				if(pastLocations.size()>20){
					pastLocations.remove(0);
				}
				rc.move(nextDirection);
				return;
			}
			if(rc.getType().canClearRubble()){
				//failed to move, look to clear rubble
				if(rc.senseRubble(nextLocation)>=GameConstants.RUBBLE_OBSTRUCTION_THRESH){
					rc.clearRubble(nextDirection);
				}
			}
		}
	}
	
	
	public static Direction computeRTAStar(MapLocation target) throws GameActionException{
		Direction dir = rc.getLocation().directionTo(target);
		int bestFValue=0;
		Direction nextDirection = dir;
		for(int deltaD:tryDirections){
			Direction newDirection = Direction.values()[(dir.ordinal()+deltaD+8)%8];
			MapLocation newLocation = rc.getLocation().add(newDirection);	
			int newFValue = rc.getLocation().distanceSquaredTo(newLocation) + rc.getLocation().distanceSquaredTo(target);
			if(newFValue<bestFValue){
				nextDirection = newDirection;
				bestFValue=newFValue;
			}
		}
		return nextDirection;
	}
	
}
