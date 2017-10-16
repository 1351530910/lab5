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
		
		//to finish this when light mode is finished in order to save some cpu costs
		while (Global.colorSensorSwitch) {
			
			//using median filter
			for (int i = 0; i < filter.length; i++) {
				Global.colorProvider.fetchSample(Global.colorData, 0);
				filter[i] = Global.colorData[0];
			}
			Arrays.sort(filter);
			Global.currentColor = filter[MID];
			if (filter[MID]<Global.colorThreshhold) {
				Global.blackLineDetected = true;
				if (!Global.turning) {
					switch ((int)(Global.angle+20)/90) {
					case 0:
						Global.X++;
						break;
					case 1:
						Global.Y++;
						break;
					case 2:
						Global.X--;
						break;
					case 3: 
						Global.Y--;
						break;
					case 4:
						Global.X++;
						break;
					default:
						break;
					}
					try {
						Thread.sleep(SLEEP_TIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else {
				Global.blackLineDetected = false;
			}
			
			
		}
	}

}
