package lab5;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class main {

	public static class Global {// a class that contains some global variables
		// switches
		public static boolean colorSensorSwitch = false;
		public static boolean odometerSwitch = false;
		public static boolean usSwitch = false;
		public static boolean turning = false;
		// motors
		public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

		// ussensors
		public static UltraSonicSensor usSensorThread;
		public static Port usPort;
		public static SensorModes usSensor;
		public static SampleProvider usDistance;
		public static float[] usData;
		public static float ObstacleDistance = 0;

		// light sensor
		public static ColorSensor colorSensorThread;
		public static Port colorSensorPort;
		public static EV3ColorSensor colorSensor;
		public static float[] colorData;
		public static SampleProvider colorProvider;
		public static float currentColor = 0;
		public static float startingColor = 0;
		public static float colorThreshhold = 0;
		public static boolean blackLineDetected = false;

		// odometer
		public static Odometer odometer;
		public static double theta = 0;

		// constants
		public static final double WHEEL_RADIUS = 2.116;
		public static final double TRACK = 10.4;
		public static final int ROTATING_SPEED = 75;
		public static final int MOVING_SPEED = 200;
		public static final double ROBOT_LENGTH = 13.8;
		public static final int COLOR_SENSOR_OFFSET_ANGLE = 17;
		public static final double SQUARE_LENGTH = 30.5;
		public static final int KEEP_MOVING = Integer.MAX_VALUE;
		public static final int STOP_MOVING = 0;

		// positionnning
		public static int X, Y = 0;
		public static double angle = 90;

		// display
		public static String firstLine = "";
		public static String secondLine = "";
		public static String thirdLine = "";
		public static String forthLine = "";
		public static String fifthLine = "";

		// variable for this lab
		public static int startingX = 0, startingY = 0;
	}

	public static void main(String[] args) {
		// initializing display
		Display t = new Display();
		Global.firstLine = "INITIALIZING";
		t.start();

		// initializing odometer
		Global.odometer = new Odometer();

		// initializing us sensor
		Global.usPort = LocalEV3.get().getPort("S1");
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			// TODO: handle exception
		}
		Global.usSensor = new EV3UltrasonicSensor(Global.usPort);
		Global.usDistance = Global.usSensor.getMode("Distance");
		Global.usData = new float[Global.usDistance.sampleSize()];

		// initializing light sensor
		Global.colorSensorPort = LocalEV3.get().getPort("S2");
		Global.colorSensor = new EV3ColorSensor(Global.colorSensorPort);
		Global.colorProvider = Global.colorSensor.getRedMode();
		Global.colorData = new float[Global.colorProvider.sampleSize() + 1];

		// initializing threads
		Global.usSensorThread = new UltraSonicSensor();
		Global.colorSensorThread = new ColorSensor();
		Global.odometer = new Odometer();
		
		//get a starting value for color sensor
		Global.colorSensorSwitch = true;
		Global.colorSensorThread.start();
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			// TODO: handle exception
		}
		Global.startingColor = Global.currentColor;
		Global.colorThreshhold = Global.startingColor/2;
		Global.colorSensorSwitch = false;

		
		setXY();
		
		Navigation mainthread= new Navigation();
		mainthread.start();
	}

	static void setXY() {
		// goto some point
		Global.firstLine = "Setting";
		Global.secondLine = "x = " + Global.startingX;
		Global.thirdLine = "y = " + Global.startingY;

		while (true) {
			switch (Button.waitForAnyPress()) {
			case Button.ID_UP:
				Global.startingY++;
				break;
			case Button.ID_DOWN:
				Global.startingY--;
				break;
			case Button.ID_LEFT:
				Global.startingX--;
				break;
			case Button.ID_RIGHT:
				Global.startingX++;
				break;
			default:
				return;
			}
		}
	}
	
	//code from early labs
		private static int convertAngle(double radius, double width, double angle) {
			return convertDistance(radius, Math.PI * width * angle / 360.0);
		}

		private static int convertDistance(double radius, double distance) {
			return (int) ((180.0 * distance) / (Math.PI * radius));
		}

		public static void move(double distance, boolean immediatereturn) throws Exception {

			Global.leftMotor.setSpeed(Global.MOVING_SPEED);
			Global.rightMotor.setSpeed(Global.MOVING_SPEED);
			if (distance<0) {
				distance*=-1;
				Global.leftMotor.rotate(-convertDistance(Global.WHEEL_RADIUS, distance), true);
				Global.rightMotor.rotate(-convertDistance(Global.WHEEL_RADIUS, distance), immediatereturn);
			}
			else {
				Global.leftMotor.rotate(convertDistance(Global.WHEEL_RADIUS, distance), true);
				Global.rightMotor.rotate(convertDistance(Global.WHEEL_RADIUS, distance), immediatereturn);
			}
			
			Thread.sleep(20);
		}

		public static void turn(double angle,boolean immediatereturn) throws Exception {
			Global.turning = true;
			Global.leftMotor.setSpeed(Global.ROTATING_SPEED);
			Global.rightMotor.setSpeed(Global.ROTATING_SPEED);
			if (angle>0) {
				Global.leftMotor.rotate(convertAngle(Global.WHEEL_RADIUS, Global.TRACK, angle), true);
				Global.rightMotor.rotate(-convertAngle(Global.WHEEL_RADIUS, Global.TRACK, angle), immediatereturn);
			}
			else {
				angle*=-1;
				Global.leftMotor.rotate(-convertAngle(Global.WHEEL_RADIUS, Global.TRACK, angle), true);
				Global.rightMotor.rotate(convertAngle(Global.WHEEL_RADIUS, Global.TRACK, angle), immediatereturn);
			}
			Global.turning = false;
			Thread.sleep(50);
		}
}
