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
		
		float filter[] = new float[COUNT_MAX];
		
		while(true) {
			//save some cpu costs
			if (Global.colorSensorSwitch) {
				
				//using median filter
				for (int i = 0; i < filter.length; i++) {
					Global.colorProvider.fetchSample(Global.colorData, 0);
					filter[i] = Global.colorData[0];
				}
				Arrays.sort(filter);
				Global.currentColor = filter[MID];
				if (filter[MID]<Global.colorThreshhold) {
					Global.blackLineDetected = true;
				}
				else {
					Global.blackLineDetected = false;
				}
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
