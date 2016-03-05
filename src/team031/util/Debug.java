package team031.util;

import battlecode.common.Clock;
import battlecode.common.RobotController;

/**
 * Created by jdshen on 1/4/16.
 */
public class Debug {
    public final static boolean on = false;
    private static RobotController rc;
    private static int last = 0;

    public static void init(RobotController rc) {
        Debug.rc = rc;
    }

    public static int diff() {
        if (on) {
            int next = Clock.getBytecodeNum();
            int diff = next - last;
            last = next;
            return diff;
        }

        return 0;
    }

    public static void setIndicatorString(int i, String s) {
        if (on) {
            rc.setIndicatorString(i, "Round " + rc.getRoundNum() + ":" + s);
        }
    }

    public static void println(Object o) {
        if (on) {
            System.out.println(o);
        }
    }

    public static void failFast(Exception e) {
        if (on) {
            e.printStackTrace();
            StringBuilder s = new StringBuilder();

            s.append(e + "\n");
            StackTraceElement[] trace = e.getStackTrace();
            for (StackTraceElement traceElement : trace) {
                s.append("\tat " + traceElement + "\n");
            }
            rc.addMatchObservation(s.toString());
            rc.resign();
        }
    }
}
