package team031.controllers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Signal;
import team031.actors.Actor;
import team031.actors.Soldier;
import team031.update.Signals;
import team031.update.Update;

/**
 * Created by jdshen on 1/4/16.
 */
public class SoldierController extends Controller {
    private Actor actor;
    public SoldierController(RobotController rc) {
        super(rc);
        Signal signal = Signals.readOnce();
        Soldier soldier = new Soldier();
        actor = soldier;
    }

    @Override
    public void run() throws GameActionException {
        Update.update();

        boolean move = rc.isCoreReady();
        boolean attack = rc.isWeaponReady();

        boolean acted;
        acted = actor.act(move, attack);
        move &= !acted;
        attack &= !acted;
    }
}
