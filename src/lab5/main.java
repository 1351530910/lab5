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
		
		public static int SC = 1;
		
		// switches
		public static boolean colorSensorSwitch = false;
		public static boolean usSwitch = false;
		public static boolean frontColorSensorSwitch = false;
		public static boolean turning = false;
		
		// motors
		public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
		public static final EV3LargeRegulatedMotor ziplineMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
		

		// ussensors
		public static UltraSonicSensor usSensorThread;
		public static Port usPort;
		public static SensorModes usSensor;
		public static SampleProvider usDistance;
		public static float[] usData;
		public static float ObstacleDistance = 0;

		// light sensor
		public static ColorSensor colorSensorThread;
		public static Port leftColorSensorPort;
		public static EV3ColorSensor leftColorSensor;
		public static float[] leftColorData;
		public static SampleProvider leftColorProvider;
		public static float leftColor = 0;
		public static float colorThreshhold = 0;
		public static boolean BlackLineDetected = false;
		
		/*
		public static frontColorSensor frontColorSensorThread;
		public static Port frontColorSensorPort;
		public static EV3ColorSensor frontColorSensor;
		public static float[] frontColorData;
		public static SampleProvider frontColorProvider;
		public static float frontColor = 0;
		public static float R = 0;
		public static float G = 0;
		public static float B = 0;
		*/
		

		// constants
		public static final int ACCELERATION = 100;
		public static final double WHEEL_RADIUS = 2.116;
		public static final double TRACK = 10.2;
		public static final int ROTATING_SPEED = 90;
		public static final int MOVING_SPEED = 150;
		public static final double ROBOT_LENGTH = 10.1;
		public static final int COLOR_SENSOR_OFFSET_ANGLE = 25;
		public static final int COLOR_SENSOR_OFFSET_ANGLE_WITH_BLACKBAND = 35;
		public static final double SQUARE_LENGTH = 30.5;
		public static final int KEEP_MOVING = 300;
		public static final int STOP_MOVING = 0;
		public static final int THREAD_SLEEP_TIME = 1500;
		public static final int THREAD_SHORT_SLEEP_TIME = 10;
		public static final int ZIPLINE_LENGTH = 250;
		public static final int FALLING_EDGE_ANGLE = -65;
		
		// positionnning
		public static int X, Y = 0;
		public static double angle = 90;//this is the real angle of the robot

		// display
		public static String firstLine = "";
		public static String secondLine = "";
		public static String thirdLine = "";
		public static String forthLine = "";
		public static String fifthLine = "";

		// variable for this lab
		public static int startingX = 0, startingY = 0;
		public static int zipLineX = 0, zipLineY = 0;
	}

	public static void main(String[] args) {
		Display t = new Display();
		Global.firstLine = "INITIALIZING";
		t.start();

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

		// initializing light sensors
		Global.leftColorSensorPort = LocalEV3.get().getPort("S2");
		Global.leftColorSensor = new EV3ColorSensor(Global.leftColorSensorPort);
		Global.leftColorProvider = Global.leftColorSensor.getRedMode();
		Global.leftColorData = new float[Global.leftColorProvider.sampleSize() + 1];

		/*
		Global.frontColorSensorPort = LocalEV3.get().getPort("S3");
		Global.frontColorSensor = new EV3ColorSensor(Global.frontColorSensorPort);
		Global.frontColorProvider = Global.frontColorSensor.getRGBMode();
		Global.frontColorData = new float[Global.frontColorProvider.sampleSize() + 1];
		*/
		
		// initializing threads
		Global.usSensorThread = new UltraSonicSensor();
		Global.colorSensorThread = new ColorSensor();
		//Global.frontColorSensorThread = new frontColorSensor();
		
		try {
			Thread.sleep(Global.THREAD_SLEEP_TIME);
		} catch (Exception e) {
			// TODO: handle exception
		}
		Global.usSensorThread.start();
		Global.colorSensorThread.start();
		//Global.frontColorSensorThread.start();

		// get a starting value for color sensor
		Global.colorSensorSwitch = true;
		
		while(Global.leftColor==0) {
			
		}
		Global.colorThreshhold = (float)(Global.leftColor *0.7);
		Global.thirdLine = ""+Global.colorThreshhold;
		Global.colorSensorSwitch = false;

		setStartingXY();
		setZiplineXY();
		setSC();
		
		Global.rightMotor.setAcceleration(Global.ACCELERATION);
		Global.leftMotor.setAcceleration(Global.ACCELERATION);
		
		Navigation mainthread = new Navigation();
		mainthread.start();
	}

	static void setStartingXY() {
		// goto some point
		Global.firstLine = "Set starting XY";

		while (true) {
			Global.secondLine = "x = " + Global.startingX;
			Global.thirdLine = "y = " + Global.startingY;
			switch (Button.waitForAnyPress()) {
			case Button.ID_UP:
				Global.startingY++;
				Global.startingY%=12;
				break;
			case Button.ID_DOWN:
				Global.startingY--;
				Global.startingY%=12;
				break;
			case Button.ID_RIGHT:
				Global.startingX++;
				Global.startingX%=12;
				break;
			case Button.ID_LEFT:
				Global.startingX--;
				Global.startingX%=12;
				break;
			default:
				return;
			}
		}
	}
	static void setZiplineXY() {
		Global.firstLine = "Set zipline XY";

		while (true) {
			Global.secondLine = "x = " + Global.zipLineX;
			Global.thirdLine = "y = " + Global.zipLineY;
			switch (Button.waitForAnyPress()) {
			case Button.ID_UP:
				Global.zipLineY++;
				Global.zipLineY%=12;
				break;
			case Button.ID_DOWN:
				Global.zipLineY--;
				Global.zipLineY%=12;
				break;
			case Button.ID_RIGHT:
				Global.zipLineX++;
				Global.zipLineX%=12;
				break;
			case Button.ID_LEFT:
				Global.zipLineX--;
				Global.zipLineX%=12;
				break;
			default:
				return;
			}
		}
	}
	static void setSC() {
		Global.firstLine = "Set zipline XY";

		while (true) {
			Global.secondLine = "SC = " + Global.SC;
			switch (Button.waitForAnyPress()) {
			case Button.ID_UP:
				Global.SC=0;
				break;
			case Button.ID_DOWN:
				Global.SC=1;
				break;
			case Button.ID_RIGHT:
				Global.SC=2;
				break;
			case Button.ID_LEFT:
				Global.SC=3;
				break;
			default:
				return;
			}
		}
	}

	
}
