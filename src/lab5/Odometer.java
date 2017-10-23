package lab5;

import lab5.main.Global;

public class Odometer extends Thread{
	private double leftMotorTachoCount;
	private double rightMotorTachoCount;
	private double ratio = Global.WHEEL_RADIUS/Global.TRACK;
	private static final int SLEEP_TIME = 150;
	
	public Odometer() {
		
	}
	public void run() {
		Global.theta=0;
		while(Global.odometerSwitch) {
			leftMotorTachoCount = Global.leftMotor.getTachoCount();
			rightMotorTachoCount = Global.rightMotor.getTachoCount();
			Global.leftMotor.resetTachoCount();
			Global.rightMotor.resetTachoCount();
			Global.theta += Math.toDegrees(ratio*Math.toRadians(rightMotorTachoCount-leftMotorTachoCount));
			if (Global.theta<0) {
				Global.theta+=360;
			}
			if (Global.theta>360) {
				Global.theta-=360;
			}
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
			}
		}
	}
}
