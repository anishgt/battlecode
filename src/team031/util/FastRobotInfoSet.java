package team031.util;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class FastRobotInfoSet {
	public static final int HASH = 10000;
	public RobotInfo[] info = new RobotInfo[HASH];
    public RobotController rc;
    		
    		
    public FastRobotInfoSet(RobotController rc){
    	this.rc = rc;
    }
    
    public void add(RobotInfo r) {
    	int id = r.ID % HASH;
        info[id] = r;
    }
    
    public void add(RobotInfo[] bots) {
    	for(int i = bots.length; --i >=0;){
    		RobotInfo bot = bots[i];
    		info[bot.ID % HASH] = bot;
    	}
    }

    /**
     * No check for whether you have the id OR INLINE this.info[HASHED ID]
     * @param id
     * @return null if it wasn't added
     */
    public RobotInfo get(int id){
    	return info[id % HASH];
    }
    
    /**
     * returns non-null stored values of RobotInfo for passed bots
     * @param bots
     * @return
     */
    public RobotInfo[] get(int[] bots){
    	int numBots = bots.length;
		RobotInfo[] ret = new RobotInfo[numBots];
		while(numBots > 0){
			numBots--;
			ret[numBots] = info[bots[numBots] % HASH];
		}
		return ret;
    }
    
    public void clear() {
        info = new RobotInfo[HASH];
    }
    
}
