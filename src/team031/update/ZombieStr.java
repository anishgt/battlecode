package team031.update;

import battlecode.common.ZombieSpawnSchedule;
import team031.controllers.Controller;
import team031.lookups.StrengthAssessment;

/**
 * Created by jdshen on 1/19/16.
 */
public class ZombieStr {
    public static final int WINDOW = 150;
    public static final int SHIFT = 70;
    public static int[] str = new int[WINDOW];

    public static int get() {
        return str[(Controller.crc.getRoundNum() + SHIFT) % WINDOW];
    }

    public static void init() {
        int sum = 0;
        ZombieSpawnSchedule schedule = Controller.c.schedule;
        for (int i = 0; i < SHIFT; i++) {
            sum += StrengthAssessment.getStrength(schedule.getScheduleForRound(i));
            str[i] = sum;
        }
    }

    public static void update() {
        int round = Controller.crc.getRoundNum() + SHIFT;
        int mod = round % WINDOW;
        int mod0 = (mod - 1 + WINDOW) % WINDOW;
        int strength = StrengthAssessment.getStrength(Controller.c.schedule.getScheduleForRound(round));
        if (round >= WINDOW) {
            int strength0 = StrengthAssessment.getStrength(Controller.c.schedule.getScheduleForRound(round - WINDOW));
            str[mod] = str[mod0] + strength - strength0;
        } else {
            str[mod] = str[mod0] + strength;
        }
    }
}
