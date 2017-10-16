package lab5;

import lab5.main.Global;

public class Navigation extends Thread{
	static final int SENSOR_START_TIME = 100;
	public Navigation() {
		
	}
	
	public void run() {
		try {
			Positionning.run();
			
			travelTo(Global.startingX, Global.startingY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void travelTo(int x,int y) throws Exception{
		//start requiring threads
		Global.colorSensorSwitch = true;
		Global.colorSensorThread.start();
		Global.odometerSwitch = true;
		Global.odometer.start();
		Thread.sleep(SENSOR_START_TIME);
		double angle = 0;
		
		//move across x
		if (x!=Global.X) {//verify if moving in x is needed
			if (x>Global.X) {
				 angle = 0-Global.angle;
			}
			else {
				angle  = 180-Global.angle;
			}
			main.turn(angle, false);
			main.move(Global.KEEP_MOVING, true);
			while(Global.X!=x) {
				
			}
			main.move(Global.STOP_MOVING, false);
		}
		
		if (y!=Global.Y) {
			if (y>Global.Y) {
				angle = 0-Global.angle;
			}
			else {
				angle = 270-Global.angle;
			}
			main.turn(angle, false);
			main.move(Global.KEEP_MOVING, true);
			while(Global.X!=x) {
				
			}
			main.move(Global.STOP_MOVING, false);
		}
		
	}
}
