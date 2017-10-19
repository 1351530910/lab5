package lab5;

import java.util.Arrays;

import lab5.main.Global;

public class ColorSensor extends Thread {

	public static final int COUNT_MAX = 3;
	public static final int MID = 1;
	public static final int SLEEP_TIME = 1000;
	
	public ColorSensor() {
		
	}
	
	@Override
	public void run() {
		
		while(true) {
			//save some cpu costs
			if (Global.colorSensorSwitch) {
				
				Global.leftColorProvider.fetchSample(Global.leftColorData, 0);
				Global.rightColorProvider.fetchSample(Global.rightColorData, 0);
				Global.forthLine = Global.leftColorData[0]+"";
				Global.fifthLine = Global.rightColorData[0]+"";
				
			}else {
				try {
					Thread.sleep(Global.THREAD_SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
