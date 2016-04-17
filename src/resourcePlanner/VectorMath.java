package resourcePlanner;

import battlecode.common.*;

public class VectorMath {
	public static MapLocation add(MapLocation a, MapLocation b){
		return new MapLocation(a.x+b.x, a.y+b.y);
	}
	public static MapLocation sub(MapLocation a, MapLocation b){
		return new MapLocation(a.x+b.x, a.y+b.y);
	}
	public static MapLocation scalMul(MapLocation a, double v){
		return new MapLocation( (int)Math.round(a.x*v), (int)Math.round( a.y*v));
	}
	public static int dotProduct(MapLocation a, MapLocation b){
		return a.x*b.x + a.y*b.y;
	}
	public static int sqnorm(MapLocation a){
		return a.x*a.x + a.y*a.y;
	}
}
