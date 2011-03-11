package main.old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindowActionListener implements ActionListener{

	MainWindow window;
	
	public MainWindowActionListener(MainWindow window){
		this.window = window;
		this.window.getStartButton().addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(window.getStartButton())){
			ServerInitializer server = new ServerInitializer();
			server.startServer();
		}
	}

}
