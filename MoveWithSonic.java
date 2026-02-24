import lejos.nxt.*;
import lejos.robotics.navigation.*;

public class MoveWithSonic {
	DifferentialPilot pilot;
	UltrasonicSensor ult = new UltrasonicSensor(SensorPort.S4);

	public static void main(String[] args) {
		MoveWithSonic traveler = new MoveWithSonic();

		traveler.pilot = new DifferentialPilot(3.1, 17.25, Motor.A, Motor.C);
		traveler.pilot.setTravelSpeed(15);
		Button.waitForPress();
		
		traveler.run();
	}
	public void run(){
		int dist = ult.getDistance();
		while (!Button.ESCAPE.isPressed()){
			while(dist > 15){
				pilot.forward();
				dist = ult.getDistance();
				LCD.drawInt(dist, 0, 0);
				if(Button.ESCAPE.isPressed()){
					System.exit(0);
				}
			}
			if(dist <= 15){
				pilot.travel(-10);
				pilot.rotate(80);
				dist = ult.getDistance();
			}
			
		}
	}
}