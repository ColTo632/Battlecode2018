// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

public class MapHandler {
	private Unit u;
	private MapSurface ms;
	private PlanetMap PM;
	private GameController gc;
	
		public MapHandler(Unit u, MapSurface ms, GameController gc)
		{
			this.gc = gc;
			this.u = u;
			this.ms = ms;
			PM = gc.startingMap(gc.planet());
		}
		
		
		public Direction walkOnGrid(int direction)
		{
			MapLocation unitLocation = u.location().mapLocation();			
			for(Direction d: Direction.values()){
				MapLocation newML = unitLocation.add(d);
				//if the node exists and has no text and is pathable
				if(PM.onMap(newML) && ms.Surface[newML.getX()][newML.getY()] == ms.Surface[unitLocation.getX()][unitLocation.getY()] + direction && PM.isPassableTerrainAt(newML) == 1 && gc.canMove(u.id(), d)){
					//record it
					return d;
				}			
			}
			
			return Direction.Center;
		}
}