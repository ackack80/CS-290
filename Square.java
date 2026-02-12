import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class Square {

	DifferentialPilot pilot;
	public void turn()
	{
		pilot.travel(100, true);
		pilot.rotate(90);
		while (pilot.isMoving())
		{
			if (Button.ESCAPE.isPressed())
				pilot.stop();
		}
		System.out.println(" " + pilot.getMovement().getAngleTurned());
		
	}
	
	public void move()
	{
		pilot.travel(100);
		while (pilot.isMoving())
		{
			if (Button.ESCAPE.isPressed())
				pilot.stop();
		}
		System.out.println(" " + pilot.getMovement().getDistanceTraveled());
		
	}
	
	public static void main(String[] args)
	{
		Square traveler = new Square();
		
		traveler.pilot = new DifferentialPilot(3.1, 17.25,  Motor.A, Motor.C);
		traveler.pilot.setTravelSpeed(30);
		Button.waitForPress();
		for(int i=0; i<4;i++)
		{
			traveler.move();
			traveler.turn();
		}
		Button.waitForPress();
	}
}
