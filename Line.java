import lejos.nxt.*;
import lejos.util.TextMenu;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.Color;

public class Line {

    public static void main(String[] args) throws Exception {

        // --- Select Sensor Port ---
        String ports[] = { "Port 1", "Port 2", "Port 3", "Port 4" };
        TextMenu portMenu = new TextMenu(ports, 1, "Sensor port");
        int portNo = portMenu.select();
        if (portNo < 0) return;

        ColorSensor cs = new ColorSensor(SensorPort.getInstance(portNo));

        // --- Use red floodlight for reflected light ---
        cs.setFloodlight(Color.RED);

        // --- Prompt user to place sensor on line ---
        LCD.clear();
        LCD.drawString("Place on line", 0, 0);
        LCD.drawString("Press ENTER", 0, 1);
        Button.ENTER.waitForPress();
        int lineValue = cs.getLightValue();

        // --- Prompt user to place sensor on floor ---
        LCD.clear();
        LCD.drawString("Place on floor", 0, 0);
        LCD.drawString("Press ENTER", 0, 1);
        Button.ENTER.waitForPress();
        int floorValue = cs.getLightValue();

        // --- Compute threshold and midpoint ---
        int threshold = Math.abs(floorValue - lineValue) / 2;
        int midValue = (lineValue + floorValue) / 2;

        LCD.clear();
        LCD.drawString("Calibration done", 0, 0);
        LCD.drawString("Line: " + lineValue, 0, 1);
        LCD.drawString("Floor: " + floorValue, 0, 2);
        LCD.drawString("Following line...", 0, 4);
        Thread.sleep(1000);

        // --- Set up motors ---
        NXTRegulatedMotor leftMotor = Motor.B;
        NXTRegulatedMotor rightMotor = Motor.C;
        DifferentialPilot pilot = new DifferentialPilot(5.6, 16.0, leftMotor, rightMotor);
        pilot.setTravelSpeed(10); // base speed

        int lastDirection = 1; // last turn direction: 1 = right, -1 = left
        int maxSteer = 100;    // maximum steering value

        // --- Main line-following loop ---
        while (!Button.ESCAPE.isPressed()) {

            int current = cs.getLightValue();
            LCD.drawInt(current, 4, 0, 4);

            int error = current - midValue;
            double turn;

            // --- Line-loss recovery ---
            if (Math.abs(current - lineValue) > threshold * 2) {
                // Sensor far from line, search in last known direction
                turn = maxSteer * lastDirection;
            } else {
                // Proportional steering
                double Kp = 5.0;
                turn = Kp * error;

                if (turn > maxSteer) turn = maxSteer;
                if (turn < -maxSteer) turn = -maxSteer;

                lastDirection = (turn > 0) ? 1 : -1;
            }

            pilot.steer(turn);
            Thread.sleep(20);
        }

        // Stop motors when finished
        pilot.stop();
        LCD.clear();
        LCD.drawString("Stopped", 0, 0);
    }
}