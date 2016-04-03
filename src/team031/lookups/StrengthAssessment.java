package team031.lookups;

import battlecode.common.RobotType;
import battlecode.common.ZombieCount;
import team031.controllers.Controller;

/**
 * Created by jdshen on 1/19/16.
 */
public class StrengthAssessment {
    public static int[] STR = calcStrength();

    public static int[] calcStrength() {
        RobotType[] types = RobotType.values();
        int[] str = new int[types.length];
        for (int i = types.length - 1; i >= 0; i--) {
            str[i] = calcStrength(types[i]);
        }

        return str;
    }

    public static int getStrength(ZombieCount[] zombies) {
        int str = 0;
        double mult = RobotType.STANDARDZOMBIE.getOutbreakMultiplier(Controller.crc.getRoundNum());
        mult = mult * mult;
        for (int i = zombies.length - 1; i >= 0; i--) {
            ZombieCount zombie = zombies[i];
            RobotType type = zombie.getType();
            str += STR[type.ordinal()] * zombies[i].getCount() * mult;
        }

        return str;
    }

    public static int getStrength(RobotType type) {
        return STR[type.ordinal()];
    }

    private static int calcStrength(RobotType type) {
        if (type.attackDelay == 0) {
            return 0;
        }

        return (int) (type.attackPower * type.maxHealth / type.attackDelay / type.movementDelay * type.attackRadiusSquared);
    }

    public static int getStrength(int[] types) {
        int str = 0;
        for (int i = types.length - 1; i >= 0; i--) {
            str += STR[i] * types[i];
        }

        return str;
    }
}
