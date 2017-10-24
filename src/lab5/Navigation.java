package lab5;

import java.util.Timer;

import lab5.main.Global;
import lejos.hardware.Button;

public class Navigation extends Thread {

	public Navigation() {

	}

	public void run() {
		try {
			Global.firstLine = "navigating";
			Global.secondLine = "";
			Global.thirdLine = "";
			
			FallingEdge();
			lightPosition();

			checkSC();
			travelTo(Global.startingX, Global.startingY);
			
			Button.waitForAnyPress();
			travelZipLine();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void checkSC() throws Exception{
		switch (Global.SC) {
		case 0:
			turn(-90, false);
			Global.X = 1;
			Global.Y = 7;
			return;
		case 1:
			Global.X = 1;
			Global.Y = 1;
			return;
		case 2:
			turn(90, false);
			Global.X = 7;
			Global.Y = 1;
			return;
		case 3:
			turn(180, false);
			Global.X = 7;
			Global.Y = 7;
			return;

		default:
			break;
		}
	}
	public void travelZipLine() throws Exception {
		Global.ziplineMotor.setSpeed(Global.MOVING_SPEED);
		Global.ziplineMotor.backward();
		if (Global.startingX == Global.zipLineX) {
			if (Global.startingY > Global.zipLineY) {
				turn(-90, false);
			} else {
				turn(90, false);
			}
		} else {
			if (Global.startingX > Global.zipLineX) {
				turn(180, false);
			}
		}
		move(Global.ZIPLINE_LENGTH, false);
		Global.ziplineMotor.stop();
	}

	public void travelTo(int x, int y) throws Exception {
		//this is working only if we start from the starting point
		// start requiring threads
		Global.colorSensorSwitch = true;
		Global.secondLine = "travel to " + x + "," + y;
		Global.thirdLine = "current: "+Global.X+"_"+Global.Y;
		Thread.sleep(Global.THREAD_SLEEP_TIME);
		
		//because our color sensor is behind the robot
		
		
		// move across x
		Global.thirdLine = "travel x";
		if (x != Global.X) {// verify if moving in x is needed

			//moving forward
			if (x > Global.X) {
				move(-30,false);//wall correction
				
				move(Global.KEEP_MOVING, true);//keep moving forward
				
				while (Global.X <= x) {//count the blacklines traveled and stop when the destination is reached
					Global.forthLine = ""+Global.X;
					if (Global.BlackLineDetected) {
						Global.BlackLineDetected = false;
						Global.X++;
						Thread.sleep(Global.THREAD_SHORT_SLEEP_TIME);
					}
					
				}
				move(-Global.ROBOT_LENGTH, false);//position the robot to the center
			} 
			//moving backward
			else {
				move(30,false);//wall correction
				
				move(-Global.KEEP_MOVING, true);//keep moving backward
				
				while (Global.X >= x) {//count the blacklines traveled and stop when the destination is reached
					Global.forthLine = ""+Global.X;
					if (Global.BlackLineDetected) {
						Global.BlackLineDetected = false;
						Global.X--;
						Thread.sleep(Global.THREAD_SHORT_SLEEP_TIME);
					}
				}
				
				move(-Global.ROBOT_LENGTH, false);//position the robot to the center
			}
		}
		
		//turn to the correct direction using the black lines
		turn(-Global.KEEP_MOVING, true);
		Thread.sleep(Global.THREAD_SLEEP_TIME);
		Global.BlackLineDetected = false;
		while (!Global.BlackLineDetected) {

		}
		turn(Global.COLOR_SENSOR_OFFSET_ANGLE, false);
		
		//update display
		Global.thirdLine = "travel y;";
		Global.forthLine = ""+Global.Y;
		
		
		// move across y
		if (y != Global.Y) {//if moving in y is needed
			while(Global.leftMotor.isMoving()) {
				
			}
			if (y > Global.Y) {
				move(-30,false);//wall correction
				
				move(Global.KEEP_MOVING, true);//keep moving forward
				
				while (Global.Y <= y) {//count the blacklines traveled and stop when the destination is reached
					Global.forthLine = ""+Global.Y;
					if (Global.BlackLineDetected) {
						Global.BlackLineDetected = false;
						Global.Y++;
						Thread.sleep(Global.THREAD_SHORT_SLEEP_TIME);
					}
				}
			} 
			else {
				move(30,false);//wall correction
				
				move(-Global.KEEP_MOVING, true);//keep moving backward
				
				while (Global.Y >= y) {//count the blacklines traveled and stop when the destination is reached
					Global.forthLine = ""+Global.Y;
					if (Global.BlackLineDetected) {
						Global.BlackLineDetected = false;
						Global.Y--;
						Thread.sleep(Global.THREAD_SHORT_SLEEP_TIME);
					}
				}
			}
		}
		
		
		move(-Global.ROBOT_LENGTH, false);//reposition the robot the the center
		
		// turn and rescan the angle
		turn(90, false);
		Global.colorSensorSwitch = false;
		
		//wall correction
		move(-30,false);
		move(30,false);
	}

	public void FallingEdge() throws Exception{
		Global.rightMotor.setAcceleration(3000);
		Global.leftMotor.setAcceleration(3000);
		//start the corresponding sensor thread
		Global.odometerSwitch = true;
		Global.usSwitch = true;
		Global.odometerThread = new Odometer();
		Global.usSensorThread = new UltraSonicSensor();
		Thread.sleep(Global.THREAD_SLEEP_TIME);
		Global.usSensorThread.start();
		Global.odometerThread.start();
		Thread.sleep(Global.THREAD_SLEEP_TIME);
				
		
		int Angle = 0;
		
		//make sure there is no wall in front
		while (Global.ObstacleDistance<Global.USThreshhold) {
			turn(90,false);
		}
		
		//make the robot face a wall
		turn(Global.KEEP_MOVING, true);
		while(Global.ObstacleDistance>Global.USThreshhold) {
		}
		turn(Global.STOP_MOVING, false);
		
		//set this angle as starting angle
		for (int i = 0; i < 5; i++) {
			Global.theta=0;
		}
		
		//redo same thing for other side
		turn(-90, false);
		turn(-Global.KEEP_MOVING, true);
		while(Global.ObstacleDistance>Global.USThreshhold) {
		}
		turn(Global.STOP_MOVING, false);
		
		//read angle and make it positive
		Angle = (int)Global.theta;
		
		//divide by 2 and add 45
		if (Angle>360) {//small correction to make sure it make no big cercles
			Angle-=360;
		}
		Angle = Angle>>1;
		Angle+=45;

		
		turn(Angle, false);
		Global.odometerSwitch = false;
		Global.usSwitch = false;
		Global.rightMotor.setAcceleration(Global.ACCELERATION);
		Global.leftMotor.setAcceleration(Global.ACCELERATION);
	}

	public void lightPosition() throws Exception {
		// start the corresponding sensor thread
		Global.colorSensorSwitch = true;
		Global.secondLine = "light positionning";
		Thread.sleep(Global.THREAD_SLEEP_TIME); // wait color sensor to get its values

		
		
		// reset X
		// move until sensor sees black line
		move(Global.KEEP_MOVING, true);
		Global.BlackLineDetected = false;
		while (!Global.BlackLineDetected) {
		}
		
		// move back to black line
		move(-Global.ROBOT_LENGTH, false);
		Thread.sleep(250);

		// reset angle
		// turn until color sensor sees a black line then turn to 90 degree
		turn(-Global.KEEP_MOVING, true);
		while (!Global.BlackLineDetected) {}
		turn(Global.COLOR_SENSOR_OFFSET_ANGLE, false);

		
		
		
		// reset Y
		// move until sensor sees black line
		move(Global.KEEP_MOVING, true);
		Global.BlackLineDetected = false;
		while (!Global.BlackLineDetected) {
		}
		
		// move back to black line
		move(-Global.ROBOT_LENGTH, false);
		Thread.sleep(250);

		// turn off color sensor
		Global.colorSensorSwitch = false;

		// wait color sensor is turned off
		Thread.sleep(200);

		turn(92, false);
		// reset coordinates
		Global.angle = 0;
	}
	

	private int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	private int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public void move(double distance, boolean immediatereturn) throws Exception {

		Global.leftMotor.setSpeed(Global.MOVING_SPEED);
		Global.rightMotor.setSpeed(Global.MOVING_SPEED);

		Global.leftMotor.rotate(convertDistance(Global.WHEEL_RADIUS, distance), true);
		Global.rightMotor.rotate(convertDistance(Global.WHEEL_RADIUS, distance), immediatereturn);

		Thread.sleep(Global.THREAD_SHORT_SLEEP_TIME);
	}

	public void turn(double angle, boolean immediatereturn) throws Exception {
		// clockwise positive
		Global.turning = true;
		Global.leftMotor.setSpeed(Global.ROTATING_SPEED);
		Global.rightMotor.setSpeed(Global.ROTATING_SPEED);
		if (angle > 0) {
			Global.leftMotor.rotate(convertAngle(Global.WHEEL_RADIUS, Global.TRACK, angle), true);
			Global.rightMotor.rotate(-convertAngle(Global.WHEEL_RADIUS, Global.TRACK, angle), immediatereturn);
		} else {
			angle *= -1;
			Global.leftMotor.rotate(-convertAngle(Global.WHEEL_RADIUS, Global.TRACK, angle), true);
			Global.rightMotor.rotate(convertAngle(Global.WHEEL_RADIUS, Global.TRACK, angle), immediatereturn);
		}
		Global.turning = false;
		Thread.sleep(Global.THREAD_SHORT_SLEEP_TIME);
	}
}
