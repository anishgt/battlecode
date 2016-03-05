package team031.util;

import battlecode.common.MapLocation;

public class FastIterableLocSet implements LocSet{
    private int size = 0;
    private StringBuilder keys = new StringBuilder();

    private int OFFSET_X = Constants.MAX_MAP_OFFSET * 3;
    private int OFFSET_Y = Constants.MAX_MAP_OFFSET;

    private String locToStr(MapLocation loc) {
        return ""+(char)(loc.x+ OFFSET_X) + (char)(loc.y + OFFSET_Y);
    }

    public void add(MapLocation loc) {
        String key = locToStr(loc);
        if (keys.indexOf(key) == -1) {
            keys.append(key);
            size++;
        }
    }

    public void remove(MapLocation loc) {
        String key = locToStr(loc);
        int index = keys.indexOf(key);
        if (index != -1) {
            keys.delete(index, index+2);
            size--;
        }
    }

    public boolean contains(MapLocation loc) {
        return keys.indexOf(locToStr(loc)) != -1;
    }

    public void clear() {
    	size = 0;
        keys = new StringBuilder();
    }

    public MapLocation getKey() {
        if (size > 0) {
            return new MapLocation(keys.charAt(0) - OFFSET_X, keys.charAt(1) - OFFSET_Y);
        }

        return null;
    }

    public MapLocation[] getKeys() {
        MapLocation[] locs = new MapLocation[size];
        for (int i = 0; i < size; i++) {
            locs[i] = new MapLocation(keys.charAt(i*2) - OFFSET_X, keys.charAt(i*2+1) - OFFSET_Y);
        }
        return locs;
    }

    public void replace(String newSet) {
        keys = new StringBuilder();
        keys.append(newSet);
        size = newSet.length() / 2;
    }
    
    public int size() {
    	return size;
    }
}