// import the API.
// See xxx for the javadocs.
import bc.*;
import java.util.*;

public class Player {

    private static int holder = 0;

    //Holds all pth objects for robots that are currently enroute.
    private static List<Path> activePaths = new LinkedList<Path>(); 

    public static void main(String[] args) {
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());

        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));

        // Connect to the manager, starting the game
        GameController gc = new GameController();

        Pathfinder pf = new Pathfinder();

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();

        while (true) {
            System.out.println("Current round: "+gc.round());

            VecUnit units = gc.myUnits();
            for (int i = 0; i < units.size(); i++) {
                Unit unit = units.get(i);

                activateRobot(unit);

                moveRobots();

                // Most methods on gc take unit IDs, instead of the unit objects themselves.
                if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), Direction.Southeast) && (holder % 2) == 0) {
                    gc.moveRobot(unit.id(), Direction.Southeast);
                }

                else if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), Direction.Northwest)) {
                    gc.moveRobot(unit.id(), Direction.Northwest);
                }

                holder++;
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
            if (path.continuePath())
                it.remove();
        }
    }

    public static void activateRobot(Unit unit) {
        UnitType type = unit.unitType()
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
        return;
    }

}