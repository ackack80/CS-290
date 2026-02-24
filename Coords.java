import lejos.nxt.*;
import lejos.robotics.navigation.*;
public class Coords {
	public static void main(String[] args){
		DifferentialPilot pilot = new DifferentialPilot(3.1, 17.25,  Motor.A, Motor.C);
		NavPathController nav = new NavPathController(pilot);
		Button.waitForPress();
		LCD.drawString("Dest: 20, 20", 0, 0);
		LCD.drawString("Press to continue", 0, 0);
		Button.waitForPress();
		LCD.clear();
		
		nav.goTo(20,20);
		LCD.drawString("Dest: 0, 0", 0, 0);
		LCD.drawString("Press to continue", 0, 0);
		Button.waitForPress();
		LCD.clear();
		
		nav.goTo(0,0);
		LCD.drawString("Dest: -20, 20", 0, 0);
		LCD.drawString("Press to continue", 0, 0);
		Button.waitForPress();
		LCD.clear();
		
		nav.goTo(-20,20);
		LCD.drawString("Dest: 0, 0", 0, 0);
		LCD.drawString("Press to continue", 0, 0);
		Button.waitForPress();
		LCD.clear();
		
		nav.goTo(0,0);
	}
}
