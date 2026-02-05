import lejos.nxt.*;
import lejos.util.*;

public class demos {
	Stopwatch sw = new Stopwatch();
	public static void main(String[] args) {
		LCD.drawString("Basic Motor Test", 0, 0);
		Button.waitForPress();
		LCD.clear();
		Motor.A.forward();
		LCD.drawString("FORWARD", 0, 0);
		Button.waitForPress();
		LCD.clear();
		Motor.A.backward();
		LCD.drawString("BACKWARD", 0, 1);
		Button.waitForPress();
		LCD.clear();
		Motor.A.stop();

		LCD.drawString("Inertia Test", 0, 0);
		Button.waitForPress();
		LCD.clear();
		Motor.A.setSpeed(360);
		Motor.A.forward();
		int count = 0;
		while (count < 360)
			count = Motor.A.getTachoCount();
		Motor.A.stop();
		LCD.drawInt(count, 0, 1);
		while (Motor.A.getRotationSpeed() > 0);
		LCD.drawInt(Motor.A.getTachoCount(), 7, 1);
		Button.waitForPress();
		LCD.clear();

		LCD.drawString("Rotation Test", 0, 0);
		Button.waitForPress();
		LCD.clear();
		new demos().go();

		LCD.clear();
		LCD.drawString("Rotation Interupt", 0, 0);
		Button.waitForPress();
		LCD.clear();
		new demos().go1();

		LCD.clear();
		LCD.drawString("Regulate Test", 0, 0);
		Button.waitForPress();
		LCD.clear();
		new demos().go3();
		
	}

	public void go() {
		Motor.A.setSpeed(360);
		rotate();
	}

	public void rotate() {
		Motor.A.rotate(360);
		LCD.drawInt(Motor.A.getTachoCount(), 4, 0, 1);
		Motor.A.rotateTo(0);
		LCD.drawInt(Motor.A.getTachoCount(), 4, 0, 2);
		Button.waitForPress();
	}

	public void go1() {

		System.out.println("Interrupt \nrotation");
		Sound.twoBeeps();
		Button.waitForPress();
		LCD.clear();
		Motor.A.rotate(360, true);
		while (Motor.A.isMoving()) {
			LCD.drawInt(Motor.A.getTachoCount(), 4, 0, 1);
			if (Button.readButtons() > 0)
				Motor.A.stop();
		}
		while (Motor.A.getRotationSpeed() > 0);
		LCD.drawInt(Motor.A.getTachoCount(), 4, 8, 1);
		Button.waitForPress();
	}
	
	public void go3() {
		Motor.A.setSpeed(720);
		Motor.C.setSpeed(720);
		step();
	}
	
	public void step()
	{
		LCD.clear();
		sw.reset();
		Motor.A.resetTachoCount();
		Motor.C.resetTachoCount();
		Motor.A.forward();
		Motor.C.forward();
		for(int r = 0 ; r<8; r++)
		{
		while(sw.elapsed() < 200* r)
		Thread.yield();
		LCD.drawInt(Motor.A.getTachoCount(),5,r);
		LCD.drawInt(Motor.C.getTachoCount(),10,r);
		}
		Motor.A.stop();
		Motor.C.stop();
		Button.waitForPress();
		}
}
