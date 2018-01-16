// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

public class Player {
	
	//Important global variables
    private static MapLocation base; 
    private static MapLocation enemyBase;

    private static long MAX_WORKER_COUNT = 15; 
    private static long workerCount = 0;
	private static long thisTurnsWorkerCount = 0;

    private static short FACTORY_THRESHHOLD = 120;
    private static short RANGER_THRESHHOLD = 20;

    private static HashMap<Integer, MapSurface> mapHolder = new HashMap<Integer, MapSurface>();
    private static HashMap<Unit, MapHandler> mapFinder = new HashMap<Unit, MapHandler>();

	private static PlanetMap PM;  
	private static List<MapLocation> resourceDeposits;

    private static GameController gc;	
	private static Team ourTeam;
	
	public static MapHandler explorationMap;
	
	public static void main(String[] args) {
		
    	System.out.println("Init Player");

        // Connect to the manager, starting the game
    	gc = new GameController();
    	ourTeam = gc.team();
    	PM = gc.startingMap(gc.planet());
    	
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Rocket);         
        gc.queueResearch(UnitType.Ranger);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Worker);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Rocket);
        gc.queueResearch(UnitType.Ranger);

    	System.out.println("Player for " + PM.getPlanet());

    	resourceDeposits = initalizeResources();
			
			explorationMap = new MapHandler(null, null, gc);
    	// We need to set our base location here

		while (true) {				
			//workerCount = gc.senseNearbyUnitsByType(new MapLocation(Planet.Earth, 0,0), 100, UnitType.Worker).size();
			System.out.println("Current round: "+gc.round() +" workerCount: "+ workerCount+" k: "+ gc.karbonite());
			thisTurnsWorkerCount = 0;

			VecUnit explorers = gc.senseNearbyUnitsByType(new MapLocation(Planet.Earth, 0,0), 2500, UnitType.Ranger);
			
			
			//exshplorr
			List<MapLocation> exploredPlaces = new ArrayList<MapLocation>();
			for (int i = 0; i < explorers.size(); i++) {
				Unit unit = explorers.get(i);				
				if(unit.team() == ourTeam){
					exploredPlaces.add(unit.location().mapLocation());
				}
			}			
			MapSurface explorationSurface = new MapSurface(PM, exploredPlaces);
			explorationMap.ms = explorationSurface;
			
			
			
			VecUnit units = gc.myUnits();
			for (int i = 0; i < units.size(); i++) {
				Unit unit = units.get(i);

				updateResources();

				activateUnit(unit);
			}
			workerCount = thisTurnsWorkerCount;
			// Submit the actions we've done, and wait for our next turn.
			gc.nextTurn();
		}
    }    

    public static void activateUnit(Unit unit) {
        UnitType type = unit.unitType();
        switch (type) { 
            case Factory: 
                activateFactory(unit);
                break;
            case Healer:
                activateHealer(unit);
                break;
            case Knight:
                activateKnight(unit);
                break;
            case Mage:
                activateMage(unit);
                break;
            case Ranger:
                activateRanger(unit);
                break;
            case Rocket:
                activateRocket(unit);
                break;
            case Worker:
                activateWorker(unit);
				thisTurnsWorkerCount++;
                break;
        }
    }

    public static void activateFactory(Unit unit) {

        // If no workers exist build one.  
        if ((workerCount == 0) && (gc.canProduceRobot(unit.id(), UnitType.Worker))) {
            gc.produceRobot(unit.id(), UnitType.Worker);
        }

        // If we have engough money build a ranger.
        else if ((gc.karbonite() >= RANGER_THRESHHOLD) && (gc.canProduceRobot(unit.id(), UnitType.Ranger))) {
            gc.produceRobot(unit.id(), UnitType.Ranger);
        }

        // If we have any units in the garrison unload and activate them.
        if (unit.structureGarrison().size() != 0) {
            for(Direction direction : Direction.values()) {
                if (gc.canUnload(unit.id(), direction)){
                    gc.unload(unit.id(), direction);
                    activateUnit(gc.senseUnitAtLocation(unit.location().mapLocation().add(direction)));
                }
            }
        }

        return;
    }

    public static void activateHealer(Unit unit) {
        return;
    }

    public static void activateKnight(Unit unit) {
        return;
    }

    public static void activateMage(Unit unit) {
        return;
    }

    public static void activateRanger(Unit unit) {
		Location location = unit.location();
		if(!location.isOnMap()){
			return;
		}
		MapLocation unitLocation = location.mapLocation();
        if (PM.onMap(unitLocation)){
            VecUnit nearby = gc.senseNearbyUnits(unitLocation, 70);
				for (int i = 0; i < nearby.size(); i++) {
					Unit other = nearby.get(i);
					if(other.team() != ourTeam && gc.isAttackReady(unit.id())){										
						//System.out.println("ranger done spotted a badguy");
						if (gc.canAttack(unit.id(), other.id())){
							//System.out.println("ranger can attack that guy");
							if(unit.location().isOnMap() && other.location().isOnMap()){
								//System.out.println("ranger gon beatemup");
								gc.attack(unit.id(), other.id());
								break;
							}
						}
					}
				}
				if(gc.senseNearbyUnitsByType(unit.location().mapLocation(), 60, UnitType.Ranger).size() > 0)
				{
					moveUnit(unit, explorationMap.walkOnGrid(-1, unit));					
				}
				
			}
        return;
    }

    public static void activateRocket(Unit unit) {
        return;
    }
		
	public static void activateWorker(Unit unit) {
        Location location = unit.location();

        if (!location.isOnMap()) {
            return;
        }

        MapLocation unitLocation = location.mapLocation();
        VecUnit adjacentFactories = gc.senseNearbyUnitsByType(unitLocation, 1, UnitType.Factory);

        // Retreat ( isVisible currently unimplemented)
        if (isVisible(unitLocation)) {
            //Find the closest enemy

            //run away from them. 

        } 

        else {

            // Replicate
            if ((workerCount < MAX_WORKER_COUNT) && (gc.karbonite() > 15)) {
                for (Direction direction : Direction.values()) {
                    if (gc.canReplicate(unit.id(), direction)){
                        workerCount++;
                        gc.replicate(unit.id(), direction);
                        activateWorker(gc.senseUnitAtLocation(unitLocation.add(direction)));
                        break;
                    }
                }
            }

            // Build new Factory
            else if ((gc.karbonite() > FACTORY_THRESHHOLD) && adjacentFactories.size() == 0 ){
                for (Direction direction : Direction.values()) {
                    if (gc.canBlueprint(unit.id(), UnitType.Factory, direction)){
                        gc.blueprint(unit.id(), UnitType.Factory, direction);
                        break;
                    }
                }
            }

            // Interact with adjacent factories
            else if (adjacentFactories.size() != 0) {

                // Repair an adjacent factory
                for (int i = 0; i < adjacentFactories.size(); i++) {
                    Unit factory = adjacentFactories.get(i);

                    if (gc.canRepair(unit.id(), factory.id())) {
                        gc.repair(unit.id(), factory.id());
                        break;
                    }
                }

                // Work on an adjacent factory
                for (int i = 0; i < adjacentFactories.size(); i++) {
                    Unit factory = adjacentFactories.get(i);

                    if (gc.canBuild(unit.id(), factory.id())) {
                        gc.build(unit.id(), factory.id());
                        break;
                    }
                }
            }

            // Harvest Karbonite
            for (Direction direction : Direction.values()) {
                if (gc.canHarvest(unit.id(), direction)) {
                    gc.harvest(unit.id(), direction);
                    break;
                }
            }

            // Find a factory to work on
            VecUnit nearbyFactories = gc.senseNearbyUnitsByType(unitLocation, 10, UnitType.Factory);
            long distance = 2500;
            MapLocation target = null;
            for (int i = 0; i < nearbyFactories.size(); i++) {
                Unit factory = nearbyFactories.get(i);

                if (factory.health() < factory.maxHealth()) {
                    MapLocation factoryLocation = factory.location().mapLocation();

                    long travelDistance = unitLocation.distanceSquaredTo(factoryLocation);
                    if  (travelDistance < distance) {
                        distance = travelDistance;
                        target = factoryLocation;
                    }
                }
            }

            if (target != null) {
                updateHashMaps(unit, target);
                Direction dir = mapFinder.get(unit).walkOnGrid(-1); 
                moveUnit(unit, dir);
                return;
            }

            // Find Karbonite to harvest 
            distance = 2500;
            target = null;
            for (MapLocation resourceLocation : resourceDeposits) {
                long travelDistance = unitLocation.distanceSquaredTo(resourceLocation);
                if  (travelDistance < distance) {
                    distance = travelDistance;
                    target = resourceLocation;
                }
            }

            if (target != null) {
                updateHashMaps(unit, target);
                Direction direction = mapFinder.get(unit).walkOnGrid(-1); 
                moveUnit(unit, direction);
                return;
            }
        }
        return;
    }

    public static void updateHashMaps(Unit unit, MapLocation location) {
		Integer coordinates = location.getX() * 100 + location.getY();
        if (!mapHolder.containsKey(coordinates)) {
            mapHolder.put(coordinates, new MapSurface(PM, location));
        }
        MapSurface currentMapSurface = mapHolder.get(coordinates);

        if (!mapFinder.containsKey(unit)) {
            mapFinder.put(unit, new MapHandler(unit, currentMapSurface, gc));
        } 
        else {
            MapHandler mh = mapFinder.get(unit);
            mh.ms = currentMapSurface;
        }
    }


    // TODO:
    public static boolean isVisible(MapLocation loc) {
        return false;
    }


    // TODO:
    public static ArrayList<MapLocation> initalizeResources() {
        return new ArrayList<MapLocation>();

    }

    // TODO:
    public static void updateResources() {
        return;
    }

    public static void moveUnit(Unit unit, Direction direction) {
        if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), direction)) {
            gc.moveRobot(unit.id(), direction);
        }
        return;
    }
}