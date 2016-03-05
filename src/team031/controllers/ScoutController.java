package team031.controllers;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Signal;
import team031.actors.Scouter;
import team031.messaging.MessageType;
import team031.messaging.Relayer;
import team031.update.HostileBuckets;
import team031.update.Signals;
import team031.update.Update;

/**
 * Created by jdshen on 1/4/16.
 */
public class ScoutController extends Controller {
    private Scouter scouter;

    private boolean[] messageTypeSet = Relayer.createSet(new MessageType[] {
        MessageType.ZOMBIE_DEN_RELAY, MessageType.ARCHON_RELAY, MessageType.TURRETS_RELAY, MessageType.ZOMBIES_RELAY,
        MessageType.PARTS_RELAY, MessageType.NEUTRAL_RELAY, MessageType.OPP_RELAY, MessageType.HELP_ARCHON
    });

    public ScoutController(RobotController rc) {
        super(rc);
        scouter = new Scouter();

        Signal sig = Signals.readOnce();
    }

    @Override
    public void run() throws GameActionException {
        Update.update();
        HostileBuckets.update();
        Relayer.relay(Signals.sigs, messageTypeSet);

        boolean move = rc.isCoreReady();
        boolean attack = rc.isWeaponReady();

        boolean acted = false;

        acted = scouter.act(move, attack);
        move &= !acted;
        attack &= !acted;
    }
}
