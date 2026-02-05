import lejos.nxt.*;
import lejos.util.*;


public class MotorTest {
	public static void main(String[]args)
	{
		LCD.drawString("Motor Test", 0, 0);
		Button.waitForPress();
		LCD.clear();
		new MotorTest().go();
	}
	
	public void go()
	{
		Motor.A.setSpeed(720);
		Motor.C.setSpeed(720);
		step();
	}

	
	public void step()
	{
		LCD.clear();
		LCD.drawString("Forward", 0, 1);
		Motor.A.forward();
		Motor.C.forward();
		
		Button.waitForPress();
		
		LCD.clear();
		LCD.drawString("Backward", 0, 1);
		Motor.A.backward();
		Motor.C.backward();
		
		Button.waitForPress();
		Motor.A.stop();
		Motor.C.stop();
	}
	
}
