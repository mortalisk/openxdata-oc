package UI;
import java.io.*;
import org.omevac.openclinica.convert.*;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
 

/** this class manages all the activities **/


public class UIActivity {

	private OdmXform xformOb;
	private String ODMFpath=null;
	private String XFormFile=null;
	private static File ODMfile, userXFormF;
	private String ODMfileStr= null;
	private String XFormfileStr= null;
	private int STATE=0;
	
	public UIActivity()
	{
		ODMfile = new File("");
		userXFormF = new File("");
		xformOb = new OdmXform();
	}
	
	/** this method sets  a selected file by the user **/
	public void set_File(File f)
	{
		ODMfile = f;
		
	}
	/** this method retrieves a previously selected file by the user **/
	public static File get_File()
	{
		return ODMfile;
	}
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
	/** this method sets xform definition file **/
	public void set_UserXformFile(File xfrm)
	{
		userXFormF = xfrm;
	}
	/** this method retrieves xform definition file **/
	public File get_UserXformFile()
	{
		return userXFormF;
	}
	/**sets the returned xform string **/
	public void store_XformStr(String xfrmstr)
	{
		if(xfrmstr.equals(null))
		{
			setStateto(0);
		}
		else
		{
			XFormfileStr = xfrmstr;
			setStateto(1);
		}
	}
	
	public void  setStateto(int state)
	{
		STATE= state;
	}
	public int gateState()
	{
		return STATE;
	}
	/**gets the xform string **/
	
	public String get_XformStr()
	{
		return XFormfileStr;
	}
	
	/**this method gets the string format of user selected file**/
	
	public void getFileStringFormat(String fpath)
	{
		ODMfile = new File(fpath);
		try
		{
			String content = FileUtils.readFileToString(ODMfile);
			store_fstring(content);
			ConvertOdmfile(getStored_fstring());
			System.out.println("this is file content: "+content);
			
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
	/**this method takes file string of an ODM file after **/
	/**it has been converted to string format**/
	
	public void ConvertOdmfile(String fstring)
	{
		if(fstring != null)
		{
		store_XformStr(xformOb.fromOdm2Xform(fstring));
	/**creates a file for the xform definition**/
		write_XformStr(get_XformStr());
		
		}
		else
		{
			System.out.println("please specify select the file to convert");
		}
	}
	
	/** method for writing and Xform definition file **/
	public void write_XformStr(String xfrmstr)
	{
		File f = new File(get_UserXformFile().getAbsolutePath());
		
		try
		{
		 FileUtils.writeStringToFile(f,xfrmstr); 	
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void establishConn(String username,String passwrd,String dbase,String Url)
	{
		System.out.println(username+" "+passwrd+" "+dbase+" "+Url);
			//xformOb.dbconnection(username, passwrd, dbase, Url);
	}
	
}
