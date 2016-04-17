package resourcePlanner;

import java.util.ArrayList;

import battlecode.common.*;

public class PathFinding extends Robot {
	
	static ArrayList<MapLocation> pastLocations = new ArrayList<>();

	public static void move(MapLocation target, Direction forward) throws GameActionException{
		if(rc.isCoreReady()){
			Direction nextDirection = greedyPathFinding(target);
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
	
	
	public static Direction greedyPathFinding(MapLocation target) throws GameActionException{
		Direction dir = rc.getLocation().directionTo(target);
		int bestFValue=Integer.MAX_VALUE;
		Direction nextDirection = dir;
		for(int deltaD:tryDirections){
			Direction newDirection = Direction.values()[(dir.ordinal()+deltaD+8)%8];
			MapLocation newLocation = rc.getLocation().add(newDirection);	
			int newFValue = rc.getLocation().distanceSquaredTo(newLocation) + rc.getLocation().distanceSquaredTo(target);
			if(newFValue<bestFValue && rc.canMove(newDirection) && !pastLocations.contains(newLocation)){
				nextDirection = newDirection;
				bestFValue=newFValue;
			}
		}
		return nextDirection;
	}
	
    public static void bugPathing(MapLocation currentLoc, MapLocation goal) throws GameActionException {
    	if(rc.isCoreReady()){
    		if (rc.getLocation().distanceSquaredTo(goal)<=2){
    				return;
    		}
	        Direction forward = currentLoc.directionTo(goal);
	        Direction right = forward.rotateRight();
	        Direction left = forward.rotateLeft();
	        if (rc.canMove(forward)) {
	            rc.move(forward);
	        } else if (rc.canMove(right)) {
	            rc.move(right);
	        } else if (rc.canMove(right.rotateRight())) {
	            rc.move(right.rotateRight());
	        } else if (rc.canMove(right.rotateRight().rotateRight())) {
	            rc.move(right.rotateRight().rotateRight());
	        }else if (rc.canMove(left)) {
	            rc.move(left);
	        }else if (rc.canMove(left.rotateLeft())) {
	            rc.move(left.rotateLeft());
	        }else if (rc.canMove(forward.opposite())) {
	            rc.move(forward.opposite());
	        }
	    }
    	return;
    }
    
}
