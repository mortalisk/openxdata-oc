package main.old;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

public class MainWindow extends JFrame {

	JButton startButton;
	
	public MainWindow(){
		CreateComponents();
		Labels();
		AddComponents();
		MakeFrame();
	}
	
	public JButton getStartButton() {
		return startButton;
	}

	private void Labels(){
		this.setTitle("Sliver - BPEL & SOAP Server");
		this.startButton.setText("Start Server");
	}
	
	private void AddComponents(){
		this.add(startButton);
	}
	
	private void setUpComponents(){
		startButton.setSize(200, 100);
	}
	
	private void MakeFrame(){
		this.setSize(640, 480);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		//this.getContentPane().setLayout(new BoxLayout(rootPane, 2));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void CreateComponents(){
		startButton = new JButton();
	}
}
