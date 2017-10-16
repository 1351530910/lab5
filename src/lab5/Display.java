package lab5;

import lab5.main.Global;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class Display extends Thread {

	public static int SLEEP_TIME = 500;
	
	public Display() {
	}
	
	public void run() {
		final TextLCD t = LocalEV3.get().getTextLCD();
		
	
		while (true) {
			t.clear();
			t.drawString(Global.firstLine, 0, 0);
			t.drawString(Global.secondLine, 0, 1);
			t.drawString(Global.thirdLine, 0, 2);
			t.drawString(Global.forthLine, 0, 3);
			t.drawString(Global.fifthLine, 0, 4);
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch bl
			}
		}
	}

	

}
