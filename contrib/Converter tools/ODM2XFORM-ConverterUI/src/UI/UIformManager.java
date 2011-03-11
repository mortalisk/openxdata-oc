package UI;
import java.io.*;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.*;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.io.IOException;
import javax.swing.JOptionPane;
import UI.Organiser.*;
import javax.swing.JPanel;


	
/** Author gbro**/
/** this class manages all the forms and their controls **/

public class UIformManager 
{

	private JFrame Oframe,cframe;
	private JLabel usernameL,paswrdL,dbaseL,serverL;
	private JTextField path, usernameT,dbaseT,serverT;
	//private JComboBox dbaseT,serverT; 
	private JPasswordField paswrdT;
	private JButton browse,ok,cancle,conect,Concancle;
	private JFileChooser fchooser,fSave;
	private FileFilter filte;
	private String discstring;
	private JMenu fmenu, settings, help;
	private JMenuItem mOpen,mConvert,mClose,sConn;
	private UIActivity activityOb;
	private JOptionPane msges;
	private GridLayout grdlayout;
	private GBHelper pos; 
	private static final int gap =5;
	private JPanel SFramContent, CFramContent;
	
	/**class constructor **/
	public UIformManager()
	{
		path = new JTextField(20);
		browse = new JButton(" ");
		ok = new JButton(" ");
		cancle = new JButton(" ");
		Concancle = new JButton(" ");
		fchooser = new JFileChooser();
		fSave = new JFileChooser();
		discstring = new String();
		activityOb = new UIActivity();
		msges = new JOptionPane();
		
	}
	
	/**this is a starting form**/
	
	public JFrame StartFrame()
	{
		
		JFrame Sframe = new JFrame("ODM-XForm Converter");
		SFramContent = new JPanel(new GridBagLayout());
		CFramContent = new JPanel(new GridBagLayout());
		
		pos = new GBHelper();
		
		JMenuBar menubar= new JMenuBar();
		fmenu = new JMenu("File");
		settings = new JMenu("Setting");
		help = new JMenu("Help");
		mOpen = new JMenuItem("Open");
		mConvert = new JMenuItem("Convert");
		mClose = new JMenuItem("Close");
		sConn = new JMenuItem("Connect");
		
		
		fmenu.setMnemonic(KeyEvent.VK_F);
		fmenu.getAccessibleContext().setAccessibleDescription("menu Items");
		settings.setMnemonic(KeyEvent.VK_S);
		settings.getAccessibleContext().setAccessibleDescription("tool connection settings");
		help.setMnemonic(KeyEvent.VK_H);
		help.getAccessibleContext().setAccessibleDescription("help features of the tool and tutorials");
		
		menubar.add(fmenu);
		menubar.add(settings);
		menubar.add(help);
		fmenu.add(mOpen);
		fmenu.add(mConvert);
		fmenu.add(mClose);
		settings.add(sConn);
		path = new JTextField(40);
		browse = new JButton("Browse");
		ok = new JButton("Convert");
		cancle = new JButton("Cancle");
		
		Sframe.setJMenuBar(menubar);
		
		/**========panel content added==========**/
		SFramContent.add(path,pos.expandW());
		SFramContent.add(browse,pos.nextCol());
		SFramContent.add(new Gap(gap),pos.nextRow());
		SFramContent.add(new Gap(gap),pos.nextRow());
		SFramContent.add(ok,pos.nextCol());
		SFramContent.add(new Gap(gap),pos.nextRow());
		SFramContent.add(new Gap(gap),pos.nextRow());
		SFramContent.add(cancle,pos.nextCol());
		
		Sframe.add(SFramContent);
		
		EventControlHandler handler = new EventControlHandler();
		path.addActionListener(handler);
		browse.addActionListener(handler);
		ok.addActionListener(handler);
		cancle.addActionListener(handler);
		mOpen.addActionListener(handler);
		mConvert.addActionListener(handler);
		mClose.addActionListener(handler);
		sConn.addActionListener(handler);
		Sframe.pack();
		Sframe.setLocation(getCenteredCorner(Sframe));
		Oframe = Sframe;
		return Sframe;	
	}
	public Point getCenteredCorner(Component c)
    {
		  
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(
            (int)((dim.getWidth() - c.getWidth()) / 2),
            (int)((dim.getHeight() - c.getHeight()) / 2)
            
        );
      
    }
	
	/**=========method for connection frame=======**/
	
	public JFrame connFrame()
	{
		String [] dbases = {"openclinica","Testing"};
		String [] servers = {"localhost","//10.10.1.3/"};
		
		JFrame conframe = new JFrame("Database Connections");
		CFramContent = new JPanel(new GridBagLayout());
		GBHelper pos = new GBHelper();
		
		usernameL = new JLabel("Username",JLabel.LEFT);
		paswrdL = new JLabel("Password",JLabel.LEFT);
		dbaseL = new JLabel("Select Database",JLabel.LEFT);
		serverL = new JLabel("Server Address",JLabel.LEFT);
		usernameT = new JTextField(30);
		paswrdT = new JPasswordField(30);
		dbaseT = new JTextField(30);
		serverT = new JTextField(30);
		conect = new JButton("Connect");
		Concancle = new JButton("Cancle");
		
		//lab1.setToolTipText("click to look for the file");
		
		/**===== panel content added ======= **/
		
		CFramContent.add(new Gap(gap),pos.nextRow());
		CFramContent.add(usernameL,pos.nextRow());
		CFramContent.add(usernameT,pos.nextCol().expandW());
		CFramContent.add(conect,pos.nextCol().nextCol());
		CFramContent.add(new Gap(gap),pos.nextRow());
		CFramContent.add(paswrdL,pos.nextRow());
		CFramContent.add(paswrdT,pos.nextCol().expandW());
		CFramContent.add(new Gap(gap),pos.nextRow());
		CFramContent.add(dbaseL,pos.nextRow());
		CFramContent.add(dbaseT,pos.nextCol().expandW() );
		CFramContent.add(Concancle,pos.nextCol().nextCol());
		CFramContent.add(new Gap(gap),pos.nextRow());
		CFramContent.add(serverL,pos.nextRow());
		CFramContent.add(serverT,pos.nextCol().expandW());
		CFramContent.add(new Gap(gap),pos.nextRow());
		CFramContent.add(new Gap(gap),pos.nextRow());
	
		conframe.add(CFramContent);
		EventControlHandler handler = new EventControlHandler();

		conect.addActionListener(handler);
		Concancle.addActionListener(handler);
		conframe.pack();
		conframe.setLocation(getCenteredCorner(conframe));
		cframe = conframe;
		
		
		return conframe;
	}
	
	/**this methods implements the file choosing dialogue box **/
	
	public void fileChooserDialogue()
	{
		//filte = new FileNameExtensionFilter(" ","","");
		fchooser = new JFileChooser();
		fchooser.setCurrentDirectory(new File("$HOME"));
		
		fchooser.setFileFilter(new javax.swing.filechooser.FileFilter()
		{
			public boolean accept(File f)
			{ 
				String name = "";
				
				name = f.getName().toLowerCase();
				
				return name.endsWith(".xml"); 
			}
			
			public String getDescription()
			{
				return "XML File";
				
			}
		});
		
		int retval = fchooser.showOpenDialog(Oframe);
	
		
		if(retval == JFileChooser.APPROVE_OPTION)
		{		File file = fchooser.getSelectedFile();
				
				path.setText(file.getAbsolutePath());	
				activityOb.set_Filepath(file.getAbsolutePath());
				
		}
		
	}
	
	/**this methods implements the file saving dialogue box **/
	
	public void fileSaveDialoague()
	{
	/*	fSave.setCurrentDirectory(new File("$HOME"));
		
		fSave.setFileFilter(new javax.swing.filechooser.FileFilter()
		{
			public boolean accept(File f)
			{ 
				String name = "";
				
				name = f.getName();
				
				return name.endsWith(".xhtml"); 
			}
			
			public String getDescription()
			{
				return "XhtML File";
				
			}
		}); */
		
		int retval = fSave.showSaveDialog(Oframe);
		
		if(retval == JFileChooser.APPROVE_OPTION)
		{	
			File file = fSave.getSelectedFile();
			activityOb.set_UserXformFile(file);
			
			//System.out.println("file to save here "+file);
			
		}
	}
	
	/** this event controlling class is for handling all form**/
	/** events on the forms defined in this class**/
	
	private class EventControlHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource()== path)
			{
				System.out.println("ihave passed enter");
			}
			else if(event.getSource()== browse || event.getSource()== mOpen)
			{
				fileChooserDialogue();
				System.out.println("ihave passed button");
			}
			else if(event.getSource()== cancle || event.getSource()== mClose)
			{
				System.exit(1);
			}
			else if(event.getSource()== Concancle )
			{
			
				System.exit(cframe.EXIT_ON_CLOSE);	
				
			}
			else if(event.getSource()== ok || event.getSource()== mConvert )
			{
				if(activityOb.get_Filepath()!= null)
				{
				fileSaveDialoague();
				activityOb.getFileStringFormat(activityOb.get_Filepath());
				
				if(activityOb.gateState() == 1)
				{
					msges.showMessageDialog(Oframe, "File Converted Successfully", "Confirmation", 1);
				}
				else
				{
					msges.showMessageDialog(Oframe, "File Failed","Failure", 1);
				}
				}
				else
				{
				msges.showMessageDialog(Oframe, "Missing File Path: BROWSE for the file", "Filepath", 1);
				}
			}
			else if(event.getSource() == sConn)
			{
				connFrame().setVisible(true);
			}
			else if(event.getSource() == conect)
			{
				
				if(usernameT.getText().trim().length() != 0 && paswrdT.getText().trim().length() != 0 && dbaseT.getText().trim().length() != 0 && serverT.getText().trim().length() != 0)
				{
					activityOb.establishConn(usernameT.getText(), paswrdT.getText(), dbaseT.getText(), serverT.getText());	
				}
				else
					{
					msges.showMessageDialog(cframe, "**Wrong credentials**", "Connection Error", 1);
					}	
			}
			
		}
		
	}
	
	
	
}
