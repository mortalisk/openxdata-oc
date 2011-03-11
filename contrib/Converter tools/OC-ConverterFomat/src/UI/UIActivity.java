package UI;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import OCFormatLayer.odm2OCformalise;

public class UIActivity {

	private String ODMFpath=null;
	private static File ODMfile, userOCFile;
	private String ODMfileStr= null;
	private String OCfileStr= null;
	private odm2OCformalise odmfOb;
	
	
	/** this method sets  a selected file path by the user **/
	public void set_Filepath(String fpath)
	{
		ODMFpath = fpath;
	}
	/** this method retrieves a previously selected file by the user **/
	public String get_Filepath()
	{
		return ODMFpath;
	}
	
	public void getFileStringFormat(String fpath)
	{
		ODMfile = new File(fpath);
		odmfOb = new odm2OCformalise();
		store_OCfstring(odmfOb.OCTrueformat(ODMfile));
		write_OCStrTofile(getStored_OCfstring());
		
	}
	/** this method sets a converted odm file string **/
	public void store_fstring(String st)
	{
		ODMfileStr = st;
	}
	/** this method retrieves a converted odm file string **/
	public String getStored_fstring()
	{
		return ODMfileStr;
	}
	
	/** this method sets a converted odm file string **/
	public void store_OCfstring(String st)
	{
		OCfileStr = st;
	}
	/** this method retrieves a corrected odm OC file string **/
	public String getStored_OCfstring()
	{
		return OCfileStr;
	}
	/** this method sets a corrected odm OC file **/
	public void set_UserOCFile(File OCfile)
	{
		userOCFile= OCfile;
	}
	/** this method retrieves a corrected odm OC definition file **/
	public File get_UserOCFile()
	{
		return userOCFile;
	}
	/** method for writing and OCfile content to file **/
	public void write_OCStrTofile(String OCstr)
	{
		File f = new File(get_UserOCFile().getAbsolutePath());
		
		try
		{
		 FileUtils.writeStringToFile(f,OCstr); 	
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getstate()
	{ 
	int state =odmfOb.gateState();
	
	return state;
	}
}
