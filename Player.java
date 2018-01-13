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

    // Holds all path objects for robots that are currently enroute.
    private static List<Path> activePaths = new LinkedList<Path>(); 

    // Holds the location of all Karbonite locations currently known.
    private static List<MapLocation> resourceDeposits;

    //Game controller and pathfinder
    private static Pathfinder pf;
    private static GameController gc;

    public static void main(String[] args) {
        // Connect to the manager, starting the game
        gc = new GameController();
        pf = new Pathfinder();

        resourceDeposits = initalizeResources();
        // We need to set our base location here

        while (true) {
            System.out.println("Current round: "+gc.round() +"workerCount: "+ workerCount);



            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);

                updateResources();

                activateUnit(unit);

                moveRobots();
            }

            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }

    // Progress all activate paths
    public static void moveRobots() {
    // This loop marchs all of our robots with paths forward one step and removes them from the paths list
    // when they reach their destination.
        Iterator<Path> it = activePaths.iterator();
        while(it.hasNext()) {
            Path path = it.next();
            if (path != null) {
                if (path.continuePath())
                    it.remove();
            }
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
        if ((workerCount == 0) && (unit.canProduceRobot(unit.id(), UnitType.Worker))) {
            unit.produceRobot(unit.id(), UnitType.Worker);
        }

        // If we have engough money build a ranger.
        else if ((gc.karbonite() > RANGER_THRESHHOLD) && (unit.canProduceRobot(unit.id(), UnitType.Ranger)) {
            unit.produceRobot(unit.id(), UnitType.Ranger);
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
        return;
    }

    public static void activateRocket(Unit unit) {
        return;
    }

    public static void activateWorker(Unit unit) {
        MapLocation unitLocation = unit.location().mapLocation();
        VecUnit adjacentFactories = gc.senseNearbyUnitsByType(unitLocation, 1, UnitType.Factory);

        // Retreat
        if (isVisible(unitLocation)) {
            activePaths.add(pf.findPath(unit, base));
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
            for (int i = 0; i < nearbyFactories.size(); i++) {

                Unit factory = nearbyFactories.get(i);

                if (factory.health() < factory.maxHealth()) {
                    activePaths.add(pf.findPath(unit, factory.location().mapLocation()));
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
            activePaths.add(pf.findPath(unit, target));         

        }

        return;
    }

    // TODO:
    public static boolean isVisible(MapLocation loc) {
        return false;
    }

    // TODO:
    public static List<MapLocation> initalizeResources() {
        return new LinkedList<MapLocation>();
    }

    // TODO:
    public static void updateResources() {
        return;
    }

}