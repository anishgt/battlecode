package version1;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class Archon extends Robot{

	static RobotType[] buildList = new RobotType[]{RobotType.SCOUT};//RobotType.SOLDIER,, RobotType.GUARD, RobotType.TURRET
	
	public static void archonCode() throws GameActionException {
		if(rc.isCoreReady()){
			Direction randomDir = randomDirection();
			RobotType toBuild = buildList[rnd.nextInt(buildList.length)];
			if(rc.getTeamParts()>100){
				if(rc.canBuild(randomDir, toBuild)){
					rc.build(randomDir,toBuild);
					return;
				}
			}
			

			RobotInfo[] alliesToHelp = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,rc.getTeam());
			MapLocation weakestOne = findWeakest(alliesToHelp);
			if(weakestOne!=null){
				rc.repair(weakestOne);
				return;
			}
		}
		
	}
	
	private static void createBuildList(int stage){
		if (stage==0)
			buildList = new RobotType[]{RobotType.SCOUT};
		else if (stage == 1)
			buildList = new RobotType[]{RobotType.GUARD};
		else if (stage == 2)
			buildList = new RobotType[]{RobotType.GUARD, RobotType.SOLDIER};
	}


}
