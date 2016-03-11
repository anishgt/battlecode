package version1;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer extends Robot{
	
	
	
	public static void run(RobotController rcIn){
		
		Robot.initializeRobot(rcIn);
		while(true){
			try{
				if(rc.getType()==RobotType.ARCHON){
					Archon.archonCode();
				}else if(rc.getType()==RobotType.TURRET){
					Turret.turretCode();
				}else if(rc.getType()==RobotType.TTM){
					TTM.ttmCode();
				}else if(rc.getType()==RobotType.GUARD){
					Guard.guardCode();
				}else if(rc.getType()==RobotType.SOLDIER){
					Soldier.soldierCode();
				}else if(rc.getType()==RobotType.SCOUT){
					Scout.scoutCode();
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			Clock.yield();
		}
	}


}
