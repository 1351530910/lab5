package lab5;

import lab5.main.Global;

public class frontColorSensor extends Thread {

	public frontColorSensor() {}
	
	/*
	@Override
	public void run() {
		
		while(true) {
			//save some cpu costs
			if (Global.frontColorSensorSwitch) {
				Global.frontColorProvider.fetchSample(Global.frontColorData, 0);
				Global.fifthLine = Global.frontColorData[0]+" "+Global.frontColorData[1]+" "+Global.frontColorData[2]+" ";
				Global.frontColor = Global.frontColorData[0];
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
*/
}
