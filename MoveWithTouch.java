import lejos.nxt.*;
import lejos.robotics.navigation.*;
public class MoveWithTouch {
	DifferentialPilot pilot; 
	TouchSensor touch = new TouchSensor(SensorPort.S1);
	public static void main(String[] args){
		MoveWithTouch traveler = new MoveWithTouch();

		traveler.pilot = new DifferentialPilot(3.1, 17.25, Motor.A, Motor.C);
		traveler.pilot.setTravelSpeed(15);
		Button.waitForPress();
		traveler.run();
		
	}
	
	public void run(){
		while (!Button.ESCAPE.isPressed()){
			pilot.forward();
			if (touch.isPressed()){
				pilot.travel(-10);
				pilot.rotate(90);
			}
		}
	}
}
