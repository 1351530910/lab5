package lab5;

import lab5.main.Global;



public class Positionning{
	
	//we made this static instead of threading because we want this to run in same thread with navigation
	public static void run() {
		
		try {
			Global.firstLine = "positionning";
			Global.secondLine = "falling edge";
			FallingEdge();
			Thread.sleep(200);
			Global.secondLine = "light positionning";
			lightPosition();
		} catch (Exception e) {
		}
	}
	
	
	
	public static void FallingEdge() throws Exception{
		final int threshhold = 50;
		//start the corresponding sensor thread
		Global.usSwitch = true;
		Global.odometerSwitch = true;
		Global.usSensorThread.start();
		Global.odometer.start();
		Thread.sleep(500);
				
		int Angle = 0;
		
		//make sure there is no wall in front
		while (Global.ObstacleDistance<threshhold) {
			main.turn(90,false);
		}
		
		//make the robot face a wall
		main.turn(Global.KEEP_MOVING, true);
		while(Global.ObstacleDistance>threshhold) {
			Thread.sleep(5);
		}
		main.turn(Global.STOP_MOVING, false);
		
		//set this angle as starting angle
		for (int i = 0; i < 5; i++) {
			Global.theta=0;
			Thread.sleep(1);
		}
		
		//redo same thing for other side
		main.turn(-90, false);
		main.turn(0-Global.KEEP_MOVING, true);
		while(Global.ObstacleDistance>50) {
			Thread.sleep(1);
		}
		main.turn(Global.STOP_MOVING, false);
		
		//read angle and make it positive
		Angle = (int)Global.theta;
		
		//divide by 2 and add 45
		if (Angle>360) {//small correction to make sure it make no big cercles
			Angle-=360;
		}
		Angle = Angle>>1;
		Angle+=45;

		main.turn(Angle, false);
		
		//turn off ussensor and odometer
		Global.usSwitch = false;
		Global.odometerSwitch = false;
	}
	
	public static void lightPosition() throws Exception {
		//start the corresponding sensor thread
		Global.colorSensorSwitch = true;
		Global.colorSensorThread.start();
		Thread.sleep(200);		//wait color sensor to get its values
		
		//reset X
		//move until sensor sees black line
		main.move(Global.KEEP_MOVING, true);
		while(!Global.blackLineDetected) {
			
		}
		//move back to black line
		main.move(0-Global.ROBOT_LENGTH, false);
		Thread.sleep(250);
		
		//reset angle
		//turn until color sensor sees a black line then turn to 90 degree
		main.turn(0-Global.KEEP_MOVING, true);
		while(!Global.blackLineDetected) {
			
		}
		main.turn(-Global.COLOR_SENSOR_OFFSET_ANGLE, false);
		
		//reset Y
		//move until sensor sees black line
		main.move(Global.KEEP_MOVING, true);
		while(!Global.blackLineDetected) {
			
		}
		//move back to black line
		main.move(0-Global.ROBOT_LENGTH, false);
		Thread.sleep(250);
		
		main.turn(90, false);
		
		//turn off color sensor
		Global.colorSensorSwitch = false;
		
		//wait color sensor is turned off
		Thread.sleep(200);
		
		//reset coordinates
		Global.angle = 0;
		Global.X = 0;
		Global.Y = 0;
	}
	
	
	
	
	
}
