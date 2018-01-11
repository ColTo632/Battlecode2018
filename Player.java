// import the API.
// See xxx for the javadocs.
import bc.*;

// It might be useful to define behaviors or plans for our player like this
public enum Plans{
    KNIGHTRUSH,
    RANGERNONSENSE,
    ESCAPETOMARS
}

// Similarly we will probably want to keep track of what phase in the game we are
public enum Phase{
    Early,
    Mid,
    Late
}

public class Player {
    public static void main(String[] args) {
        //This was all taken verbatim from the example player I'm not sure what we need from it. 
        /*
        // MapLocation is a data structure you'll use a lot.
        MapLocation loc = new MapLocation(Planet.Earth, 10, 20);
        System.out.println("loc: "+loc+", one step to the Northwest: "+loc.add(Direction.Northwest));
        System.out.println("loc x: "+loc.getX());
        
        // One slightly weird thing: some methods are currently static methods on a static class called bc.
        // This will eventually be fixed :/
        System.out.println("Opposite of " + Direction.North + ": " + bc.bcDirectionOpposite(Direction.North));
        */
        
        // Connect to the manager, starting the game
        GameController gc = new GameController();

        // Direction is a normal java enum.
        Direction[] directions = Direction.values();
        
        // Analyze the map and starting units and make any strategy decisions here
        Plans plan = analyzeStart();
        
        // Perform pre-planned opening. 
        begin(plan);
        
        // Begin game loop to take over turn-turn operation. 
        while (true) {
            System.out.println("Current round: "+gc.round());
            
            

            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            VecUnit units = gc.myUnits();
            // Unit logic goes here 
            for (Unit unit : units) {

                // Most methods on gc take unit IDs, instead of the unit objects themselves.
                if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), Direction.Southeast)) {
                    gc.moveRobot(unit.id(), Direction.Southeast);
                }
            }
            
            
            
            
            
            
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }

    private static void begin(Plans plan) {
        // TODO Auto-generated method stub
        Phase gamePhase = Phase.Early;
        VecUnit units = gc.myUnits();
        Unit workerChainHead = units.get(0); 
                
        while (gamephase == Phase.Ealy) {
            System.out.println("Current round: "+gc.round());
            //Early game operation goes here
            
            if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), Direction.Southeast)) {
                gc.moveRobot(unit.id(), Direction.Southeast);
            }
            workerChainHead
            

            // VecUnit is a class that you can think of as similar to ArrayList<Unit>, but immutable.
            units = gc.myUnits();
            // Unit logic goes here 
            for (Unit unit : units) {

                // Most methods on gc take unit IDs, instead of the unit objects themselves.
                if (gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), Direction.Southeast)) {
                    gc.moveRobot(unit.id(), Direction.Southeast);
                }
            }        
            
            // Submit the actions we've done, and wait for our next turn.
            gc.nextTurn();
        }
    }

    private static Plans analyzeStart() {
        // TODO Auto-generated method stub
        return null;
    }
}