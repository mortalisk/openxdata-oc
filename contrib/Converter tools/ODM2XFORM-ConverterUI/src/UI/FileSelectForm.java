package UI;
import javax.swing.JFrame;

public class FileSelectForm 
{
	/**this is the main class, which calls the form manager**/
	
	public static void main(String args[])
	{	
		UIformManager FrmObj = new UIformManager();
		FrmObj.StartFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FrmObj.StartFrame().setVisible(true);
	}

	
}
