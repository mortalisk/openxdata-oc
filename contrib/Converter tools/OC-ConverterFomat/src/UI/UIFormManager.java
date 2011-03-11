package UI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.awt.event.*;
import java.io.File;

import UI.Organiser.GBHelper;
import UI.Organiser.Gap;
import javax.swing.JPanel;

//import UI.UIformManager.EventControlHandler;

public class UIFormManager {
	
	private JFrame Oframe,cframe;
	private JPanel SFramContent, CFramContent;
	private JMenu fmenu, settings, help;
	private JMenuItem mOpen,mConvert,mClose,sConn;
	private JButton browse,ok,cancle,conect;
	private JTextField path;
	private GBHelper pos; 
	private static final int gap =5;
	private JFileChooser fchooser,fSave;
	private UIActivity activityOb;
	private JOptionPane msges;
	
	
	public UIFormManager()
	{
		path = new JTextField(20);
		browse = new JButton(" ");
		ok = new JButton(" ");
		cancle = new JButton(" ");
		fchooser = new JFileChooser();
		fSave = new JFileChooser();
		//discstring = new String();
		activityOb = new UIActivity();
		msges = new JOptionPane();
		
	}
	
	public JFrame StartFrame()
	{
		
		JFrame Sframe = new JFrame("ODM-OC Format");
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
	
	/**this methods implements center corner screen positioning **/
	
	public Point getCenteredCorner(Component c)
    {
		  
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
       
        return new Point(
            (int)((dim.getWidth() - c.getWidth()) / 2),
            (int)((dim.getHeight() - c.getHeight()) / 2)   
        );
    }
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
	
	private class EventControlHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			if(event.getSource()== path)
			{
				
			}
			else if(event.getSource()== browse || event.getSource()== mOpen)
			{
				fileChooserDialogue();
				
			}
			else if(event.getSource()== cancle || event.getSource()== mClose)
			{
				System.exit(1);
			}
			else if(event.getSource()== ok || event.getSource()== mConvert )
			{
				if(activityOb.get_Filepath()!= null)
				{
				fileSaveDialoague();
				activityOb.getFileStringFormat(activityOb.get_Filepath());
				if(activityOb.getstate() == 1)
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
		}
	}
	
	public void fileSaveDialoague()
	{
		fSave.setCurrentDirectory(new File("$HOME"));
		
		fSave.setFileFilter(new javax.swing.filechooser.FileFilter()
		{
			public boolean accept(File f)
			{ 
				String name = "";
				
				name = f.getName();
				
				return name.endsWith(".xml"); 
			}
			
			public String getDescription()
			{
				return "XML File";
				
			}
		}); 
		
		int retval = fSave.showSaveDialog(Oframe);
		
		if(retval == JFileChooser.APPROVE_OPTION)
		{	
			File file = fSave.getSelectedFile();
			activityOb.set_UserOCFile(file);
			
		}
	}
	
}
