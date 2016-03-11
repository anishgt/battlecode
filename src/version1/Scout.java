package version1;

import battlecode.common.Direction;
import battlecode.common.GameActionException;

public class Scout extends Robot{
	
	static int scoutCreationRound = -1;
	
	public static void scoutCode() throws GameActionException{
		// TODO Auto-generated method stub
		int temp = rc.getRoundNum();
		if (scoutCreationRound == -1){
			initialDirection = Direction.EAST;
					//randomDirection();
			scoutCreationRound = rc.getRoundNum();
		}	
		tryToMove(initialDirection);
		
	}

}
