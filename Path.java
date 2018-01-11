import bc.*;
import java.util.*;

public class Path {

	private Unit unit;
	private Queue<Direction> directions;
	private GameController gc;

	public Path(Unit unit, Queue<Direction> directions, GameController gc) {
		this.unit = unit;
		this.directions = directions;
		this.gc = gc;
	}

	// Robot pointed to by this path takes another step along the path. Returns true if the path is empty after moving.  
	public boolean continuePath() {

		//We may want to add a check to see if the unit is within range/nearby any enemy units before blindly proceeding 

		//Makes the robot move
		if (!directions.isEmpty() && gc.isMoveReady(unit.id()) && gc.canMove(unit.id(), directions.peek())) {
            gc.moveRobot(unit.id(), directions.poll());
        }

        return directions.isEmpty();
	}

}
