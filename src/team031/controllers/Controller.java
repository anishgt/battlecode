package team031.controllers;

import battlecode.common.*;

public abstract class Controller {
    public static Controller c;
    public static RobotController crc;

    public static void setController(Controller c) {
        Controller.c = c;
        Controller.crc = c.rc;
    }

    //shortcuts for basic information
    public final int id;
    public final Team team;
    public final Team enemy;
    public final RobotController rc;
    public final RobotType type;
    public long[] teamMemory;

    public final ZombieSpawnSchedule schedule;

    //shortcuts for type
    public final double attackPower;

    public final double attackDelay;
    public final double moveDelay;
    public final double cooldownDelay;

    public final int bytecodeLimit;

    public final int ars; // attack radius squared
    public final int srs; // sensor radius squared

    public final double maxHealth;

    public final int buildTurns;
    public final int partCost;

    //shortcuts for commonly used values
    public static final int MAX_DIAGONAL_SQ = getMaxDiagonalSq();

    private static int getMaxDiagonalSq() {
        int height = GameConstants.MAP_MAX_HEIGHT;
        int width = GameConstants.MAP_MAX_WIDTH;
        return height * height + width * width;
    }

    public Controller(RobotController rc) {
        team = rc.getTeam();
        enemy = team.opponent();
        id = rc.getID();
        type = rc.getType();
        teamMemory = rc.getTeamMemory();
        schedule = rc.getZombieSpawnSchedule();
        this.rc = rc;

        setController(this);

        attackDelay = type.attackDelay;
        moveDelay = type.movementDelay;
        cooldownDelay = type.cooldownDelay;

        attackPower = type.attackPower;
        srs = type.sensorRadiusSquared;
        ars = type.attackRadiusSquared;
        maxHealth = type.maxHealth;
        bytecodeLimit = type.bytecodeLimit;
        buildTurns = type.buildTurns;
        partCost = type.partCost;
    }

    public abstract void run() throws GameActionException;

}
