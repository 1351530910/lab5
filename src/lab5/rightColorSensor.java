package lab5;

import lab5.main.Global;

public class rightColorSensor extends Thread {

	public static long time = 0;
	public rightColorSensor() {}
	
	@Override
	public void run() {
		
		while(true) {
			//save some cpu costs
			if (Global.colorSensorSwitch) {
				
				Global.rightColorProvider.fetchSample(Global.rightColorData, 0);
				Global.rightColor = Global.rightColorData[0];
				
				if (Global.rightColor< Global.colorThreshhold) {
					Global.rightBlackLineDetected =true;					
					
					try {
						Thread.sleep(Global.THREAD_SLEEP_TIME);
					} catch (InterruptedException e) {
					}
				}else {
					Global.rightBlackLineDetected = false;
				}
			}else {
				try {
					Thread.sleep(Global.THREAD_SLEEP_TIME*2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
