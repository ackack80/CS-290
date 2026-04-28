import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.Color;

public class LineAvoid {

    public static void main(String[] args) throws Exception {

        // --- Setup sensors ---
        ColorSensor cs = new ColorSensor(SensorPort.S2);
        cs.setFloodlight(Color.RED);

        TouchSensor touch = new TouchSensor(SensorPort.S1);
        UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S4);

        // --- Setup motors and pilot ---
        NXTRegulatedMotor leftMotor = Motor.B;
        NXTRegulatedMotor rightMotor = Motor.C;
        DifferentialPilot pilot = new DifferentialPilot(5.6, 16.0, leftMotor, rightMotor);
        pilot.setTravelSpeed(10);

        // --- Line calibration ---
        LCD.clear();
        LCD.drawString("Place on line", 0, 0);
        LCD.drawString("Press ENTER", 0, 1);
        Button.ENTER.waitForPress();
        int lineValue = cs.getLightValue();

        LCD.clear();
        LCD.drawString("Place on floor", 0, 0);
        LCD.drawString("Press ENTER", 0, 1);
        Button.ENTER.waitForPress();
        int floorValue = cs.getLightValue();

        int threshold = Math.abs(floorValue - lineValue) / 2;
        int midValue = (lineValue + floorValue) / 2;

        LCD.clear();
        LCD.drawString("Calibration done", 0, 0);
        Thread.sleep(1000);

        int lastDirection = 1; // last line direction
        int maxSteer = 100;

        // --- Main loop ---
        while (!Button.ESCAPE.isPressed()) {

            int currentLight = cs.getLightValue();
            int distance = ultra.getDistance();

            // --- Obstacle detected ---
            if (touch.isPressed() || distance <= 15) {

                // Step 1: Back up
                pilot.travel(-15);

                // Step 2: Rotate off the line
                pilot.rotate(80);

                // Step 3: Move forward past the obstacle
                pilot.travel(25);

                // Step 4: Rotate back toward the line
                pilot.rotate(-80);

             // Step 5: Move forward past obstacle
                pilot.travel(50);

                // Step 6: Rotate back toward original path (roughly the line)
                pilot.rotate(-80);

                // Step 7: Controlled return to line
                pilot.forward();
                while (Math.abs(cs.getLightValue() - lineValue) > threshold) {
                    Thread.sleep(20); // keep moving straight until line is detected
                }

                // Once the line is detected, normal line-following resumes

                // Once line detected, resume normal line following
                continue;
            }

            // --- Normal line following ---
            int error = currentLight - midValue;
            double turn;

            if (Math.abs(currentLight - lineValue) > threshold * 2) {
                // Lost line, search last known direction
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

        pilot.stop();
        LCD.clear();
        LCD.drawString("Stopped", 0, 0);
    }
}