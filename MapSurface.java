import bc.*;
import java.util.*;

public class MapSurface {

	//the x coordinate of the center point of the grid
	public int CenterX;
	//the y coordinate of the center point of the grid
	public int CenterY;
	//Planetary Unique ID
	public int PUID;
	//may go unused, its just a thought
	public String Name;
	//the actual grid of shorts
	public short[][] Surface;
	

	public MapSurface(PlanetMap pmWorld, int x, int y) {
		
		System.out.println("Init MapSurface");
		CenterX = x;
		CenterY = y;
		PUID = (x * 100) + y;
		long pWidth = pmWorld.getWidth();
		long pHeight = pmWorld.getHeight();
		Planet p = pmWorld.getPlanet();
		Surface = new short[(int)pWidth][(int)pHeight];
		List<MapLocation> lstML = new ArrayList<MapLocation>();
		lstML.add(new MapLocation(p, x, y));
		short distance = 1;
		propagateMap(lstML, distance, pmWorld);
		//while there is a node in consideration
	}
	
	
	
	public MapSurface(PlanetMap pmWorld, MapLocation ml) {
		
		System.out.println("Init MapSurface");
		CenterX = ml.getX();
		CenterY = ml.getY();
		PUID = (CenterX * 100) + CenterY;
		long pWidth = pmWorld.getWidth();
		long pHeight = pmWorld.getHeight();
		Planet p = pmWorld.getPlanet();
		Surface = new short[(int)pWidth][(int)pHeight];
		List<MapLocation> lstML = new ArrayList<MapLocation>();
		lstML.add(ml);
		short distance = 1;
		propagateMap(lstML, distance, pmWorld);
		//while there is a node in consideration
	}
	
	public MapSurface(PlanetMap pmWorld, ArrayList<MapLocation> lstML) {
		
		System.out.println("Init MapSurface");
		CenterX = -1;
		CenterY = -1;
		long pWidth = pmWorld.getWidth();
		long pHeight = pmWorld.getHeight();
		Planet p = pmWorld.getPlanet();
		Surface = new short[(int)pWidth][(int)pHeight];
		short distance = -6;
		propagateMap(lstML, distance, pmWorld);
		//while there is a node in consideration
	}
	
	private void propagateMap(List<MapLocation> lstML, short distance, PlanetMap pmWorld){
		while(!lstML.isEmpty()){
			//ready a new lst for new nodes
			List<MapLocation> lstNextML = new ArrayList<MapLocation>();
			//for each node in the old list
			for(MapLocation ml: lstML){
				//if there is nothing written at this node
				//(written this way so that excess nodes do not produce children)				
				if((distance > 0 && Surface[ml.getX()][ml.getY()] < 1)){
					//set the distance at this node
					Surface[ml.getX()][ml.getY()] = distance;
					//for each adjacent node
					for(Direction d: Direction.values()){						
						MapLocation newML = ml.add(d);
						//if the node exists and has no text and is pathable
						if(pmWorld.onMap(newML) && Surface[newML.getX()][newML.getY()] < 1 && pmWorld.isPassableTerrainAt(newML) == 1){
							//record it
							lstNextML.add(newML);
						}
					}	
				}
				//block is for negative distances
				else if(Surface[ml.getX()][ml.getY()] == 0){
					//set the distance at this node
					Surface[ml.getX()][ml.getY()] = distance;
					//for each adjacent node
					for(Direction d: Direction.values()){						
						MapLocation newML = ml.add(d);
						//if the node exists and has no text and is pathable
						if(pmWorld.onMap(newML) && Surface[newML.getX()][newML.getY()] == 0 && pmWorld.isPassableTerrainAt(newML) == 1){
							//record it
							lstNextML.add(newML);
						}
					}	
				}				
			}
			//repeat with new list of nodes
			lstML = lstNextML;
			distance++;
		}
	}
}