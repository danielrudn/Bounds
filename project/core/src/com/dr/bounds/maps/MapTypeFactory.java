package com.dr.bounds.maps;

import com.dr.bounds.Player;
import com.dr.bounds.maps.maptypes.DefaultMapType;
import com.dr.bounds.maps.maptypes.ForestMapType;
import com.dr.bounds.maps.maptypes.GapMapType;
import com.dr.bounds.maps.maptypes.IceMapType;
import com.dr.bounds.maps.maptypes.MachineryMapType;
import com.dr.bounds.maps.maptypes.RotatingMapType;
import com.dr.bounds.maps.maptypes.SkyMapType;
import com.dr.bounds.maps.maptypes.SpaceMapType;
import com.dr.bounds.maps.maptypes.SpikeMapType;

public class MapTypeFactory {
	
	// map generation type
	public static final int TYPE_DEFAULT = 0, TYPE_SPACE = 1, TYPE_MACHINERY = 2, TYPE_SKY = 3, TYPE_SPIKE = 4, TYPE_ROTATE = 5, TYPE_FOREST = 6, TYPE_ICE = 7, TYPE_GAP = 8;
	// number of map types
	public static final int NUMBER_MAPS = 9;

	private MapTypeFactory() { }
	
	public static MapType getMapType(int mapID, Player player, MapGenerator gen)
	{
		if(mapID == TYPE_DEFAULT)
		{
			return new DefaultMapType(TYPE_DEFAULT, player, gen);
		}
		else if(mapID == TYPE_MACHINERY)
		{
			return new MachineryMapType(TYPE_MACHINERY, player, gen);
		}
		else if(mapID == TYPE_SPACE)
		{
			return new SpaceMapType(TYPE_SPACE, player, gen);
		}
		else if(mapID == TYPE_SKY)
		{
			return new SkyMapType(TYPE_SKY, player, gen);
		}
		else if(mapID == TYPE_SPIKE)
		{
			return new SpikeMapType(TYPE_SPIKE,player,gen);
		}
		else if(mapID == TYPE_ROTATE)
		{
			return new RotatingMapType(TYPE_ROTATE,player,gen);
		}
		else if(mapID == TYPE_FOREST)
		{
			return new ForestMapType(TYPE_FOREST, player, gen);
		}
		else if(mapID == TYPE_ICE)
		{
			return new IceMapType(TYPE_ICE, player, gen);
		}
		else if(mapID == TYPE_GAP)
		{
			return new GapMapType(TYPE_GAP, player, gen);
		}
		return null;
	}
	
}