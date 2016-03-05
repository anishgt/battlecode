package team031;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import team031.controllers.*;
import team031.messaging.Messager;
import team031.util.Debug;

public class RobotPlayer {
	public static void run(RobotController rc) {
        Debug.init(rc);
        Messager.init(rc);

		Controller cont = null;
		do {
			try {
				switch (rc.getType()) {
                    case ARCHON:
                        cont = new ArchonController(rc);
                        break;
                    case GUARD:
                        cont = new GuardController(rc);
                        break;
                    case SCOUT:
                        cont = new ScoutController(rc);
                        break;
                    case SOLDIER:
                        cont = new SoldierController(rc);
                        break;
                    case TURRET:
                        cont = new TurretController(rc);
                        break;
                    case VIPER:
                        cont = new ViperController(rc);
                        break;
                    default:
                        cont = null;
                        Debug.failFast(new RuntimeException("no type?!?!"));
                        break;
                }
			} catch (Exception e) {
                e.printStackTrace();
                Debug.failFast(e);
            }
		} while (cont == null);

		while (true) {
			try {
				cont.run();
			} catch (Exception e) {
				e.printStackTrace();
                Debug.failFast(e);
			}
			Clock.yield();
		}
	}
}