// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

public class Player {
	
	//Important global variables
    private static long MAX_WORKER_COUNT = 15;

    private List<Unit> knightList = new ArrayList<>();
    private List<Unit> mageList = new ArrayList<>();
    private List<Unit> rangerList = new ArrayList<>();

    private List<Unit> workerList = new ArrayList<>();
    private List<Unit> healerList = new ArrayList<>();

    private List<Unit> factoryList = new ArrayList<>();
    private List<Unit> rocketList = new ArrayList<>();

    private static long MAX_FACTORY_COUNT = 3;		
    private static long workerCount = 0;
	private static long thisTurnsWorkerCount = 0;

    private static short NORMAL_FACTORY_THRESHHOLD = 110;
			
    private static long factoryCount = 0;
	private static long thisTurnsFactoryCount = 0;
	
	private static boolean enemyVisible = false;
	private static boolean currentlyPileDriving = false;
		
    private static short OVERLOAD_FACTORY_THRESHHOLD = 350;
    private static short ROCKET_THRESHHOLD = 300;
    private static short RANGER_THRESHHOLD = 20;

    private static HashMap<Integer, MapSurface> mapHolder = new HashMap<Integer, MapSurface>();
    private static HashMap<Unit, MapHandler> mapFinder = new HashMap<Unit, MapHandler>();
    private static HashMap<MapLocation, Long> resourceDeposits;
	private static List<MapLocation> EnemyLocations;
		private static List<MapLocation> EnemyLocations;
		private static List<MapLocation> BaseLocations;
    private static List<MapLocation> rocketList = new LinkedList<MapLocation>();
    private static HashSet<MapLocation> landingZones = new HashSet<MapLocation>();

	private static PlanetMap PM;  
    private static AsteroidPattern strikePattern;

    private static GameController gc;	
	private static Team ourTeam;
	private static Team otherteam;
	
	public static MapHandler explorationMap;
	public static MapHandler crowdedMap;
	public static MapHandler piledriverMap;
	
	public static void main(String[] args) {
		
    	System.out.println("Init Player");

        // Connect to the manager, starting the game
    	gc = new GameController();
    	ourTeam = gc.team();
			
			if(ourTeam == Team.Red)
			{
				otherteam = Team.Blue;
			}
			else
			{
				otherteam = Team.Red;
			}
    	PM = gc.startingMap(gc.planet());

        // Queue up research if we are on earth
    	if (PM.getPlanet() == Planet.Earth) {
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
        }

    	System.out.println("Player for " + PM.getPlanet());

    	resourceDeposits = initalizeResources();
			EnemyLocations = initalizeTeamLocations(otherteam);
			BaseLocations = initalizeTeamLocations(ourTeam);
			
			
		explorationMap = new MapHandler(null, null, gc);

		crowdedMap = new MapHandler(null, null, gc);
		
		
		piledriverMap = new MapHandler(null, null, gc);

        if (PM.getPlanet() == Planet.Mars) {
            strikePattern = gc.asteroidPattern();
        }

		while (true) {	
			
			
			
						
						
			//workerCount = gc.senseNearbyUnitsByType(new MapLocation(Planet.Earth, 0,0), 100, UnitType.Worker).size();
			System.out.println("Current round: "+gc.round() +" workerCount: "+ workerCount+" k: "+ gc.karbonite());
			thisTurnsWorkerCount = 0;
			thisTurnsFactoryCount = 0;
			long round = gc.round();
			
            if ((PM.getPlanet() == Planet.Mars) && strikePattern.hasAsteroid(round)) {
                AsteroidStrike strike = strikePattern.asteroid(round);
                resourceDeposits.put(strike.getLocation(), strike.getKarbonite());
            }

			if(round % 20 == 1) {				
				pileDrive();
				deCrowd();
				AssessBasesAndEnemyLocations();
			}
			
			if(round % 80 == 1) {				
				pileDrive();
				explore();
				currentlyPileDriving = !currentlyPileDriving;
			}
			
			if((round > 200) && (round % 22 == 0)) {
                RANGER_THRESHHOLD = 320;
                NORMAL_FACTORY_THRESHHOLD = 420;
            }
			
            if((round > 200) && (round % 11 == 0)) {
                RANGER_THRESHHOLD = 20;
                NORMAL_FACTORY_THRESHHOLD = 110;
            }
						
				/*		
				if(enemyVisible && !currentlyPileDriving)
				{
					pileDrive();
					currentlyPileDriving = true;
				}
				if(!enemyVisible)
				{
					currentlyPileDriving = false;
				}
				enemyVisible = false;
			*/




			VecUnit units = gc.myUnits();
			for (int i = 0; i < units.size(); i++) {
				Unit unit = units.get(i);

				activateUnit(unit);
			}
			workerCount = thisTurnsWorkerCount;
			
			factoryCount = thisTurnsFactoryCount;
			// Submit the actions we've done, and wait for our next turn.
			gc.nextTurn();
		}
    }
		public static void explore()
		{
			VecUnit explorers = gc.senseNearbyUnitsByType(new MapLocation(Planet.Earth, 0,0), 2500, UnitType.Ranger);
			List<MapLocation> exploredPlaces = new ArrayList<MapLocation>();
			for (int i = 0; i < explorers.size(); i++) {
				Unit unit = explorers.get(i);				
				if(unit.team() == ourTeam){
					exploredPlaces.add(unit.location().mapLocation());
				}
			}			
			MapSurface explorationSurface = new MapSurface(PM, exploredPlaces);
			explorationMap.ms = explorationSurface;
		}
		
		public static void deCrowd()
		{
			VecUnit fact = gc.senseNearbyUnitsByType(new MapLocation(Planet.Earth, 0,0), 2500, UnitType.Factory);
			VecUnit rock = gc.senseNearbyUnitsByType(new MapLocation(Planet.Earth, 0,0), 2500, UnitType.Rocket);
			List<MapLocation> crowdedPlaces = new ArrayList<MapLocation>();
			for (int i = 0; i < fact.size(); i++) {
				Unit unit = fact.get(i);				
				if(unit.team() == ourTeam){
					crowdedPlaces.add(unit.location().mapLocation());
				}
			}
			for (int i = 0; i < rock.size(); i++) {
				Unit unit = rock.get(i);				
				if(unit.team() == ourTeam){
					crowdedPlaces.add(unit.location().mapLocation());
				}
			}				
			MapSurface crowdedSurface = new MapSurface(PM, crowdedPlaces);
			crowdedMap.ms = crowdedSurface;
		}
		
		public static void pileDrive()
		{
			
			VecUnit enemies = gc.senseNearbyUnitsByTeam(new MapLocation(Planet.Earth, 0,0), 2500, otherteam);
			List<MapLocation> enemyList = new ArrayList<MapLocation>();
			for (int i = 0; i < enemies.size(); i++){
				Unit unit = enemies.get(i);
				enemyList.add(unit.location().mapLocation());
				MapLocation target = unit.location().mapLocation();
				MapSurface piledriverSurface = new MapSurface(PM, target);
				piledriverMap.ms = piledriverSurface;
				
			}			
		}
		
		public static void AssessBasesAndEnemyLocations()
		{
			Iterator bases = BaseLocations.iterator();
			List<MapLocation> BaseHolder = new ArrayList<MapLocation>();
			while(bases.hasNext()){
				MapLocation b = (MapLocation) bases.next();
				if(!gc.canSenseLocation(b)){
					BaseHolder.add(b);
				}
			}			
			for(MapLocation ml: BaseHolder){
				BaseLocations.remove(ml);
			}
			
			Iterator enemyBases = EnemyLocations.iterator();
			List<MapLocation> EnemyHolder = new ArrayList<MapLocation>();
			while(enemyBases.hasNext()){
				MapLocation e = (MapLocation) enemyBases.next();
				if(gc.senseNearbyUnitsByTeam(e, 10, otherteam).size() == 0 && gc.canSenseLocation(e)){
					EnemyHolder.add(e);
				}
			}			
			for(MapLocation ml: EnemyHolder){
				
				EnemyLocations.remove(ml);
				System.out.println("removed Scary Place");
			}
			
			
			
			VecUnit enemies = gc.senseNearbyUnitsByTeam(new MapLocation(Planet.Earth, 19,19), 2500, otherteam);
			
			long greatestThreat = 0;
			MapLocation scaryThing = null;
			for (int i = 0; i < enemies.size(); i++){
				Unit unit = enemies.get(i);
				MapLocation unitLocation = unit.location().mapLocation();
				long threatLevel = 0;
				threatLevel = gc.senseNearbyUnitsByTeam(unitLocation, 20, otherteam).size();
				if(threatLevel > greatestThreat){
					scaryThing = unitLocation;
					greatestThreat = threatLevel;
				}
				
				
				
			}
			if(scaryThing != null){
				System.out.println("new Scary Place");
					EnemyLocations.add(scaryThing);
			}
			System.out.println("number of scary places is equal to " + EnemyLocations.size());
			
		}
		

    public static void activateUnit(Unit unit) {
        UnitType type = unit.unitType();
        switch (type) { 
            case Factory: 
                activateFactory(unit);
				thisTurnsFactoryCount++;
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

        //Sense surroundings 
        //    ask am i in danger? can i kill an enemy if i charge?





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
        VecUnit adjacentRockets = gc.senseNearbyUnitsByType(unitLocation, 1, UnitType.Rocket);

        VecUnit nearby = gc.senseNearbyUnits(unitLocation, 70);
				int enemiesSpotted = 0;
				int alliesSpotted = 1;
				if(gc.senseNearbyUnitsByTeam(unitLocation, 70, otherteam).size() != 0){
			for (int i = 0; i < nearby.size(); i++) {
				Unit other = nearby.get(i);
				if(other.team() != ourTeam){
					enemiesSpotted++;
					if(gc.isAttackReady(unit.id())){
						//System.out.println("ranger done spotted a badguy");
						if (gc.canAttack(unit.id(), other.id())){
							//System.out.println("ranger can attack that guy");
							if(unit.location().isOnMap() && other.location().isOnMap()){
								//System.out.println("ranger gon beatemup");
								gc.attack(unit.id(), other.id());
							}
						}
					}
				}
				else
				{
					alliesSpotted++;
				}
			}
				}
        if (adjacentRockets.size() != 0) { 
            for (int i = 0; i < adjacentRockets.size(); i++) {
                Unit rocket = adjacentRockets.get(i);

                if (gc.canLoad(rocket.id(), unit.id())) {
                    gc.load(rocket.id(), unit.id());
                    break;
                }
            }
        }

            MapLocation target = null;
        if ((gc.round() > 700) && (unitLocation.getPlanet() != Planet.Mars)) { 
            VecUnit nearbyRockets = gc.senseNearbyUnitsByType(unit.location().mapLocation(), 2500, UnitType.Rocket);
            long distance = 2500;
            for (int i = 0; i < nearbyRockets.size(); i++) {
                Unit rocket = nearbyRockets.get(i);
                if (gc.canLoad(rocket.id(), unit.id()) || gc.canRepair(rocket.id(), unit.id())) {
                    MapLocation rocketLocation = rocket.location().mapLocation();

                    long travelDistance = unitLocation.distanceSquaredTo(rocketLocation);
                    if  (travelDistance < distance) {
                        distance = travelDistance;
                        target = rocketLocation;
                    }
                }
            }

            if (target != null) {
                updateHashMaps(unit, target);
                Direction dir = mapFinder.get(unit).walkOnGrid(-1); 
                moveUnit(unit, dir);
                return;
            }
        }
				long distance = 2500;
            target = null;
						Iterator it;
						if(alliesSpotted > enemiesSpotted){
							it = EnemyLocations.iterator();
							//System.out.println("attacking Scary Place");
						}
						else {
							it = BaseLocations.iterator();
						}
            while (it.hasNext()) {
                MapLocation attackLocation = (MapLocation) it.next();

								long travelDistance = unitLocation.distanceSquaredTo(attackLocation);

                    if  (travelDistance < distance) {
                        distance = travelDistance;
                        target = attackLocation;
                    }
										
            }

            if (target != null && currentlyPileDriving) {
                updateHashMaps(unit, target);
                Direction direction = mapFinder.get(unit).walkOnGrid(-1); 
                moveUnit(unit, direction);

                return;
            }
		else if(gc.senseNearbyUnitsByType(unit.location().mapLocation(), 60, UnitType.Ranger).size() > 0) {
			moveUnit(unit, explorationMap.walkOnGrid(-1, unit));					
		}
				
        return;
    }

    public static void activateRocket(Unit unit) {
        Location location = unit.location();
        if (!location.isOnMap()) {
            return;
        }
        MapLocation unitLocation = location.mapLocation();

        if(!rocketList.contains(unitLocation)) {
            rocketList.add(unitLocation);
        }

        // If Health is low take off
        if (unit.health() <= 100) {
            MapLocation landingZone = findLandingZone();
            if ((landingZone != null) && gc.canLaunchRocket(unit.id(), landingZone)) {
                gc.launchRocket(unit.id(), landingZone);
                rocketList.remove(unitLocation);
								System.out.println("rocket left in panic");
                return;
            }
        }

        // If the rocket is full and no one is around it take off 
        else if ((gc.round() == 749) || ((unit.structureGarrison().size() == unit.structureMaxCapacity()) && (gc.senseNearbyUnitsByTeam(unitLocation, 1, ourTeam).size() == 0))) {
            MapLocation landingZone = findLandingZone();
            if ((landingZone != null) && gc.canLaunchRocket(unit.id(), landingZone)) {
                gc.launchRocket(unit.id(), landingZone);
                rocketList.remove(unitLocation);
								System.out.println("rocket left calmly");
                return;
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
        Location location = unit.location();

        if (!location.isOnMap()) {
            return;
        }

        MapLocation unitLocation = location.mapLocation();
        VecUnit adjacentFactories = gc.senseNearbyUnitsByType(unitLocation, 1, UnitType.Factory);
        VecUnit adjacentRockets = gc.senseNearbyUnitsByType(unitLocation, 1, UnitType.Rocket);
        ArrayList<Unit> adjacentStructures = new ArrayList<Unit>();
        for (int i = 0; i < adjacentFactories.size(); i++) {
            adjacentStructures.add(adjacentFactories.get(i));
        }

        // Retreat ( isVisible currently unimplemented)
        if (isVisible(unitLocation)) {
            //Find the closest enemy

            //run away from them. 

        } 

        else {

            // Replicate
            if ((workerCount < MAX_WORKER_COUNT) && (gc.karbonite() > 15) && (PM.getPlanet() != Planet.Mars)) {
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
            else if (((gc.karbonite() > NORMAL_FACTORY_THRESHHOLD && factoryCount <= MAX_FACTORY_COUNT) || gc.karbonite() > OVERLOAD_FACTORY_THRESHHOLD) && adjacentFactories.size() == 0 ){
                for (Direction direction : Direction.values()) {
                    if (gc.canBlueprint(unit.id(), UnitType.Factory, direction)){
                        gc.blueprint(unit.id(), UnitType.Factory, direction);
                        break;
                    }
                }
            }

            // Interact with adjacent factories
            else if (adjacentStructures.size() != 0) {

                // Repair an adjacent factory
                for (int i = 0; i < adjacentStructures.size(); i++) {
                    Unit structure = adjacentStructures.get(i);

                    if (gc.canRepair(unit.id(), structure.id())) {
                        gc.repair(unit.id(), structure.id());
                        break;
                    }
                }

                // Work on an adjacent factory
                for (int i = 0; i < adjacentStructures.size(); i++) {
                    Unit structure = adjacentStructures.get(i);

                    if (gc.canBuild(unit.id(), structure.id())) {
                        gc.build(unit.id(), structure.id());
																				//System.out.println("Fixing a factory");

                        break;
                    }
                }
            }

            // Build a rocket
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

            // Find a factory to work on
            VecUnit nearbyFactories = gc.senseNearbyUnitsByType(unitLocation, 50, UnitType.Factory);
            long distance = 2500;
            MapLocation target = null;
            for (int i = 0; i < nearbyFactories.size(); i++) {
                Unit structure = nearbyFactories.get(i);

                if (structure.team() == ourTeam && structure.structureIsBuilt() == 0) {
                    MapLocation factoryLocation = structure.location().mapLocation();

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
								//System.out.println("Finding a factory");
                return;
            }

            // Find Karbonite to harvest 
            distance = 2500;
            target = null;
            Iterator it = resourceDeposits.keySet().iterator();
            while (it.hasNext()) {
                MapLocation resourceLocation = (MapLocation) it.next();

                if (gc.canSenseLocation(resourceLocation)) {
                    if(gc.karboniteAt(resourceLocation) == 0) {
                        it.remove();
                    }
                }

                if (resourceDeposits.containsKey(resourceLocation)){
                    long travelDistance = unitLocation.distanceSquaredTo(resourceLocation);

                    if  (travelDistance < distance) {
                        distance = travelDistance;
                        target = resourceLocation;
                    }
                }
            }

            if (target != null) {
                updateHashMaps(unit, target);
                Direction direction = mapFinder.get(unit).walkOnGrid(-1); 
                moveUnit(unit, direction);
								//System.out.println("Finding resources");

                return;
            }

			//move away from factories and rockets
			moveUnit(unit, crowdedMap.walkOnGrid(-1, unit));
														System.out.println("decrowding");

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
    public static HashMap<MapLocation, Long> initalizeResources() {
        HashMap<MapLocation, Long> resourceDeposits = new HashMap<MapLocation, Long>();
        VecMapLocation mapLocations = gc.allLocationsWithin(new MapLocation(PM.getPlanet(), 0,0), 2500);

        for(int i = 0; i < mapLocations.size(); i++) {
            MapLocation location = mapLocations.get(i);
            long karboniteAtLocation = PM.initialKarboniteAt(location);

            if (karboniteAtLocation > 0) {
                resourceDeposits.put(location, karboniteAtLocation);
            }

        }

        return resourceDeposits;
    }
		
		public static List<MapLocation> initalizeTeamLocations(Team t) {
        List<MapLocation> TeamLocations = new ArrayList<MapLocation>();
        VecUnit initialUnits = PM.getInitial_units();
				
				for (int i = 0; i < initialUnits.size(); i++) {
					Unit unit = initialUnits.get(i);

          
					if(unit.team() == t)
					{
						MapLocation unitLocation = unit.location().mapLocation();
						TeamLocations.add(unitLocation);
						System.out.println("Added new init unit for team " + t);
					}
				}
        return TeamLocations;
    }

    public static MapLocation findLandingZone() {
			System.out.println("Finding a landing zone");
        // Pick a spot on mars
        
        PlanetMap mars = gc.startingMap(Planet.Mars);

        for (int x = 0; x < mars.getWidth(); x++) {
            for (int y = 0; y < mars.getHeight(); y++) {
								MapLocation landingZone = new MapLocation(Planet.Mars, x, y);
                if ((mars.isPassableTerrainAt(landingZone) == 1) && (!gc.canSenseLocation(landingZone) || gc.senseUnitAtLocation(landingZone) == null) && !landingZones.contains(landingZone)) {

								System.out.println("zone at " + x + " " + y + " looks ok");
                    // Check that units can be unloaded from it
                    boolean isLandingZone = false;
                    for (Direction direction : Direction.values()) {
                        MapLocation nearbySpace = landingZone.add(direction);
												if(mars.onMap(nearbySpace)){
													System.out.println("nearby zone is on map");
																isLandingZone = ((mars.isPassableTerrainAt(nearbySpace) == 1) && (!gc.canSenseLocation(landingZone) || gc.senseUnitAtLocation(landingZone) == null));
																if(isLandingZone){
																	break;
																}
												}
                    }

                    if(isLandingZone) {
                        landingZones.add(landingZone);
                        return landingZone;
                    }
										System.out.println("wasnt ok");
                }
            }  
        }  
				
								System.out.println("no landing zones to be had");
        return null;
    }

    public static void moveUnit(Unit unit, Direction direction) {
        if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), direction)) {
            gc.moveRobot(unit.id(), direction);
        }
        return;
    }
}