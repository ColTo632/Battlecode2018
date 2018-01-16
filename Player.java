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

    private static short FACTORY_THRESHHOLD = 120;
    private static short RANGER_THRESHHOLD = 20;

		private static MapSurface topleftCorner1;
		private static MapSurface topleftCorner2;
		private static MapSurface topleftCorner3;
		private static MapSurface topleftCorner4;
		//wip
		private static MapSurface workFactory;
		//wip
		private static MapSurface currentresource;
		private static PlanetMap PM;  
		private static List<MapLocation> resourceDeposits;

    private static GameController gc;	
<<<<<<< Updated upstream
		private static Team ourTeam;
		
		public static void main(String[] args) {
			
			System.out.println("Init Player");
=======
	private static Team ourTeam;
	
	public static MapHandler explorationMap;
	
	public static void main(String[] args) {
>>>>>>> Stashed changes

        // Connect to the manager, starting the game
			gc = new GameController();
			ourTeam = gc.team();
			PM = gc.startingMap(gc.planet());
			
			
			System.out.println("Player for " + PM.getPlanet());
			
			topleftCorner1 = new MapSurface(PM, 5, 5);
			topleftCorner2 = new MapSurface(PM, 15, 5);
			topleftCorner3 = new MapSurface(PM, 5, 15);
			topleftCorner4 = new MapSurface(PM, 15, 15);
			workFactory = new MapSurface(PM, 19, 1);
			currentresource = new MapSurface(PM, 1, 19);

<<<<<<< Updated upstream
			resourceDeposits = initalizeResources();
			// We need to set our base location here

=======
    	resourceDeposits = initalizeResources();
			
		explorationMap = new MapHandler(null, null, gc);
>>>>>>> Stashed changes

			while (true) {
				//workerCount = gc.senseNearbyUnitsByType(new MapLocation(Planet.Earth, 0,0), 100, UnitType.Worker).size();
<<<<<<< Updated upstream
				System.out.println("Current round: "+gc.round() +" workerCount: "+ workerCount);



=======
				System.out.println("Current round: "+gc.round() +" workerCount: "+ workerCount+" k: "+ gc.karbonite());
				thisTurnsWorkerCount = 0;

				if(gc.round() % 20 == 1)
				{
					explore();
				}
				
>>>>>>> Stashed changes
				VecUnit units = gc.myUnits();
				for (int i = 0; i < units.size(); i++) {
					Unit unit = units.get(i);

					updateResources();

					activateUnit(unit);
				}

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
                break;
        }
    }

    public static void activateFactory(Unit unit) {
        // If no workers exist build one.  
        if ((workerCount == 0) && (gc.canProduceRobot(unit.id(), UnitType.Worker))) {
            gc.produceRobot(unit.id(), UnitType.Worker);
        }

        // If we have engough money build a ranger.
        else if ((gc.karbonite() > RANGER_THRESHHOLD) && (gc.canProduceRobot(unit.id(), UnitType.Ranger))) {
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
<<<<<<< Updated upstream
			Location local = unit.location();
			if(!local.isOnMap()){
				return;
=======
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

						if (gc.canAttack(unit.id(), other.id())){

							if(unit.location().isOnMap() && other.location().isOnMap()){

								gc.attack(unit.id(), other.id());
								break;
							}
						}
					}
				}
				if (gc.senseNearbyUnitsByType(unit.location().mapLocation(), 60, UnitType.Ranger).size() > 0) {
					moveUnit(unit, explorationMap.walkOnGrid(-1, unit));					
				}
				
>>>>>>> Stashed changes
			}
			MapLocation unitLocation = unit.location().mapLocation();
            if (PM.onMap(unitLocation)){
                VecUnit nearby = gc.senseNearbyUnits(unitLocation, 70);
								for (int i = 0; i < nearby.size(); i++) {
									Unit other = nearby.get(i);
									if(other.team() != ourTeam && gc.isAttackReady(unit.id())){										
										System.out.println("ranger done spotted a badguy");
										if (gc.canAttack(unit.id(), other.id())){
											System.out.println("ranger can attack that guy");
											if(unit.location().isOnMap() && other.location().isOnMap()){
												System.out.println("ranger gon beatemup");
												gc.attack(unit.id(), other.id());
												break;
											}
										}
									}
								}
								//add stuff about how rangers move here
						}
        return;
    }

    public static void activateRocket(Unit unit) {
        Location location = unit.location();
        if (!location.isOnMap) {
            return;
        }
        MapLocation unitLocation = location.MapLocation();

        // If the rocket is full and no one is around it take off 
        if ((gc.round() == 749) || ((unit.structureGarrison().size() == unit.structureMaxCapacity) && (gc.senseNearbyUnitsByTeam(unitLocation, 1, ourTeam).isEmpty())) {
            MapLocation landingZone = findLandingZone();
            if (gc.canLaunchRocket(unit.id(), landingZone)) {
                gc.LaunchRocket(unit.id(), landingZone);
            }
        }

        // If on mars and garrison has units unload them 
        if ((unit.structureGarrison().size() != 0) && location.isOnPlanet(Planet.Mars)) {
            for(Direction direction : Direction.values()) {
                if (gc.canUnload(unit.id(), direction)){
                    gc.unload(unit.id(), direction);
                    activateUnit(gc.senseUnitAtLocation(unit.location().mapLocation().add(direction)));
                }
            }
        }

        return;
    }
		
		public static void activateWorker(Unit unit) {
        MapLocation unitLocation = unit.location().mapLocation();
        VecUnit adjacentFactories = gc.senseNearbyUnitsByType(unitLocation, 1, UnitType.Factory);

        // Retreat ( isVisible currently unimplemented)
        if (isVisible(unitLocation)) {
            //activepaths depreciated
						//activePaths.add(pf.findPath(unit, base));
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
            else if ((gc.karbonite() > FACTORY_THRESHHOLD) && adjacentStructures.size() == 0){
                for (Direction direction : Direction.values()) {
                    if (gc.canBlueprint(unit.id(), UnitType.Factory, direction)){
                        gc.blueprint(unit.id(), UnitType.Factory, direction);
                        break;
                    }
                }
            }

            // todo
            // Interact with adjacent factories OR ROCKETS
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

            // Build a rocket
            // Build new Factory
            else if ((gc.karbonite() > ROCKET_THRESHHOLD) && adjacentStructures.size() == 0){
                for (Direction direction : Direction.values()) {
                    if (gc.canBlueprint(unit.id(), UnitType.Rocket, direction)){
                        gc.blueprint(unit.id(), UnitType.Rocket, direction);
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
            //todo
            // Find a factory to work on OR A ROCKET
            VecUnit nearbyFactories = gc.senseNearbyUnitsByType(unitLocation, 10, UnitType.Factory);
            for (int i = 0; i < nearbyFactories.size(); i++) {

                Unit factory = nearbyFactories.get(i);

                if (factory.health() < factory.maxHealth()) {
									if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), walkOnGrid(unit, workFactory, gc))) {
										gc.moveRobot(unit.id(), walkOnGrid(unit, workFactory, gc));
									}
									return;
                }
            }

            // Find Karbonite to harvest 
            long distance = 2500;
            MapLocation target = null;
            for (MapLocation location : resourceDeposits) {
                long travelDistance = unitLocation.distanceSquaredTo(location);
                if  (travelDistance < distance) {
                    distance = travelDistance;
                    target = location;
                }
            }
						if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), walkOnGrid(unit, currentresource, gc))) {
							gc.moveRobot(unit.id(), walkOnGrid(unit, currentresource, gc));
						}
						return;
        }

        return;
    }

    // TODO:
    public static boolean isVisible(MapLocation loc) {
        return false;
    }

    // TODO:
    public static Location findLandingZone() {

    } 

    // TODO:
    
    public static ArrayList<MapLocation> initalizeResources() {
        return new ArrayList<MapLocation>();

    }

    // TODO:
    public static void updateResources() {
        return;
    }
		
		public static Direction walkOnGrid(Unit u, MapSurface ms, GameController gc)
		{
			MapLocation unitLocation = u.location().mapLocation();			
			for(Direction d: Direction.values()){
				MapLocation newML = unitLocation.add(d);
				//if the node exists and has no text and is pathable
				if(PM.onMap(newML) && ms.Surface[newML.getX()][newML.getY()] == ms.Surface[unitLocation.getX()][unitLocation.getY()] - 1 && PM.isPassableTerrainAt(newML) == 1 && gc.canMove(u.id(), d)){
					//record it
					return d;
				}			
			}
			
			return Direction.Center;
		}
}