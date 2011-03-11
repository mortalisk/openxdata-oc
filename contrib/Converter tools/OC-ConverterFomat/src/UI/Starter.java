package UI;

import javax.swing.JFrame;
import java.awt.*;

public class Starter {

/**this is the main class, which calls the form manager**/
	private static JFrame Fframe;
	
	public static void main(String args[])
	{	
		UIFormManager FrmObj = new UIFormManager();
		FrmObj.StartFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FrmObj.StartFrame().setVisible(true);
	}
	
	

}
