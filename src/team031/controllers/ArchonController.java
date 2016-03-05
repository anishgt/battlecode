package team031.controllers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import team031.actors.Spawner;
import team031.archon.Repairer;
import team031.update.Archon;
import team031.update.Update;
import team031.update.ZombieStr;

public class ArchonController extends Controller {
    private Repairer repairer;
    private Spawner spawner;

    public ArchonController(RobotController rc) {
        super(rc);
        Archon.init();

        spawner = new Spawner();
        repairer = new Repairer();
        ZombieStr.init();
    }

    @Override
    public void run() throws GameActionException {
        Update.update();
        ZombieStr.update();

        repairer.repair();

        boolean move = rc.isCoreReady();
        boolean attack = rc.isWeaponReady();

        boolean acted = false;

//        if (grouper.rank == 0) {
//            acted = finder.act(move, attack);
//            move &= !acted;
//            attack &= !acted;
//        }

        acted = spawner.act(move, attack);
    }

}
