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
		public static final int MOVING_SPEED = 125;
		public static final double ROBOT_LENGTH = 13.8;
		public static final int COLOR_SENSOR_OFFSET_ANGLE = 15;
		public static final double SQUARE_LENGTH = 30.5;
		public static final int KEEP_MOVING = 300;
		public static final int STOP_MOVING = 0;
		public static final int THREAD_SLEEP_TIME = 1000;
		public static final int THREAD_SHORT_SLEEP_TIME = 200;
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
			Thread.sleep(Global.THREAD_SHORT_SLEEP_TIME);
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
		try {
			Thread.sleep(Global.THREAD_SLEEP_TIME);
		} catch (Exception e) {
			// TODO: handle exception
		}
		Global.usSensorThread.start();
		Global.colorSensorThread.start();
		Global.odometer.start();

		// get a starting value for color sensor
		Global.colorSensorSwitch = true;
		try {
			Thread.sleep(Global.THREAD_SHORT_SLEEP_TIME);
		} catch (Exception e) {
		}
		while(Global.currentColor==0) {
			
		}
		Global.startingColor = Global.currentColor;
		Global.colorThreshhold = Global.startingColor / 2;
		Global.secondLine = ""+Global.startingColor;
		Global.thirdLine = ""+Global.colorThreshhold;
		Global.colorSensorSwitch = false;

		setXY();

		Navigation mainthread = new Navigation();
		mainthread.start();
	}

	static void setXY() {
		// goto some point
		Global.firstLine = "Setting";

		while (true) {
			Global.secondLine = "x = " + Global.startingX;
			Global.thirdLine = "y = " + Global.startingY;
			switch (Button.waitForAnyPress()) {
			case Button.ID_UP:
				Global.startingY = (Global.startingY + 1) % 12;
				break;
			case Button.ID_DOWN:
				Global.startingY = (Global.startingY - 1) % 12;
				break;
			case Button.ID_RIGHT:
				Global.startingX = (Global.startingX + 1) % 12;
				break;
			case Button.ID_LEFT:
				Global.startingX = (Global.startingX - 1) % 12;
				break;
			default:
				return;
			}
		}
	}

	
}
