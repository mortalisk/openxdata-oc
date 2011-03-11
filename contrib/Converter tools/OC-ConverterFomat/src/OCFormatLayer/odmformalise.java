package OCFormatLayer;


import org.apache.commons.io.IOCase;
import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;

/**
 * this class converts Openclinica ODMfile file with data to
 * the format that is acceptable for uploading back to OpenClinica system.
 *  @author gbro
**/


public class odmformalise {
	
	private static final String ItemGrpdata = "<ItemGroupData";
	private static final String Itemdata = "<ItemData";
	private static final String valuedata = "Value=\"";
	private static final String ItemGrpdataend = "</ItemGroupData>";
	private String ItemGrpdataTrueOid;
	private static final String ItemOID= "<ItemData ItemOID=";
	private static final String delStart= "-";
	private static final String rplaceVal ="Value=\"";
	private Vector vc,val2change,optnElments,ValOptions, values;
	private int numOfValues,count =0;
	private String falseString;
	
		
	
	public odmformalise()
	{
		vc =new Vector();
		val2change= new Vector();
		optnElments = new Vector();
		ValOptions = new Vector();
		values = new Vector();
	}
	
	public String OCTrueformat(File str)
	{
		IdentifyStrings(str);
		return correctFile();
	}
	/**this takes incorrect OC-ODMfile format**/
	private void IdentifyStrings(File Inpfile)
	{
		int lastBytPos =0;
		int AvlByt = 0;
		
		try
		{
		
		BufferedInputStream Binput = new BufferedInputStream(new FileInputStream(Inpfile));
		
		AvlByt = Binput.available();
		byte[] buffer = new byte[AvlByt];
			while(AvlByt != 0)
			{
				Binput.read(buffer,lastBytPos, AvlByt);
				lastBytPos+=AvlByt;
				AvlByt =Binput.available();
			}	
			String sttemp = new String(buffer);
			
			/**Tokenizing the returned string**/
			
			StringTokenizer tokens = new StringTokenizer(sttemp);
						
			while(tokens.hasMoreTokens())
			{
				vc.add(tokens.nextToken());
						
			}
			
			/**checking for OC-ODM file nodes**/
			if (vc.contains(ItemGrpdata))
			{
				falseString = vc.get(vc.indexOf(ItemGrpdata)+1).toString();
				ItemGrpdataTrueOid= getTrueOID( falseString);
				vc.set((vc.indexOf(ItemGrpdata)+1), ItemGrpdataTrueOid); //needs to be dynamic
			}
			if(vc.contains(Itemdata ))
			{
				LooKForValues();
				sortgotten( values,count);
				
			}
			else
			{
				System.out.println("The file is in wrong Format");
			}
			
		}
		catch(IOException x)
		{
			x.printStackTrace();
		}
		
	}
	
	public void LooKForValues()
	{
		int StrtIndx = vc.indexOf(Itemdata);
		int StopIndx = vc.indexOf(ItemGrpdataend);
		
		String gotten=null;
		values = new Vector();
		count=0;
				
		while(StrtIndx < StopIndx)
		{
			gotten = vc.get(StrtIndx).toString();
			
		/**identifying values in Value strings in ODM file **/	
			identifyValues( gotten, StopIndx );
			StrtIndx = StrtIndx+1;
		} 
		
	}
	public void identifyValues(String gotten,int StopIndx )
	{
		
		if(gotten.contains(valuedata))
		{
			String spacecheck;
			
			spacecheck= gotten;
				
			if((spacecheck.charAt(spacecheck.length()-1))== '0' || (spacecheck.charAt(spacecheck.length()-1))== '1' || (spacecheck.charAt(spacecheck.length()-1))== '2' || (spacecheck.charAt(spacecheck.length()-1))== '3' || (spacecheck.charAt(spacecheck.length()-1))== '4' || (spacecheck.charAt(spacecheck.length()-1))== '5' || (spacecheck.charAt(spacecheck.length()-1))== '6' || (spacecheck.charAt(spacecheck.length()-1))== '7' || (spacecheck.charAt(spacecheck.length()-1))== '8' || (spacecheck.charAt(spacecheck.length()-1))== '9')
			{
				int StrtIndx2;// = vc.indexOf(Itemdata);
				String indfy;
				
				StrtIndx2 = vc.indexOf(spacecheck);
				while(StrtIndx2 < StopIndx)
				{
					indfy= vc.elementAt(StrtIndx2).toString();
					recurseThruVC( indfy,spacecheck, StrtIndx2);
					StrtIndx2++;
				}
				LooKForValues();
				System.out.println(vc.toString());
				
			}else
			{
				values.add(gotten);
				count++;
			}
			
		}
		else
		{
			System.out.println("No Values identified");
			
		}
		
		
	}
	
	public boolean recurseThruVC(String indfy,String Checked, int coutedInd)
	{
		int i, checkedInd;
		checkedInd = vc.indexOf(Checked);
		for( i= 0; i<=9; i++)
		{
		if(indfy.equals(String.valueOf(i))|| (indfy.endsWith(String.valueOf(i)+"\"/>")&& indfy.length()==4))
		{
				vc.setElementAt(Checked+","+indfy,checkedInd );	
				if(indfy.equals(vc.elementAt(coutedInd).toString()))
				{
					vc.removeElementAt(coutedInd);
					indfy=vc.elementAt(coutedInd).toString();
					Checked = vc.elementAt(checkedInd).toString();
					// recursion
					if(recurseThruVC(indfy,Checked,coutedInd))
						return true;
				}
		}
		else
			return true;
		}	
		
		return false;
	}
	
		/** method for determining the ItemGroupOID**/	
	public String getTrueOID(String flseString)
	{
		int needed, CutPoint =0;
		needed = 0;
		String TrueOID= "";
		
		if(flseString.contains("UNGROUPED"))
		{
			CutPoint = flseString.indexOf("-");
			
			while(needed < CutPoint)
			{
				TrueOID = TrueOID + flseString.charAt(needed);
				needed++;
			}
			
			TrueOID = TrueOID + "\"";
		}
		
		return TrueOID;
	}
		/** methods takes a vector of value strings and their total number**/
	
	private void sortgotten(Vector gottenvalue, int count)
	{
		int p,i=8;
		int j=0;
		int frstEl;
		char c,d;
		char prev;
		String gotten;
		
		 setnumofValues(count);
		 
		frstEl= gottenvalue.indexOf(gottenvalue.firstElement());
		/**obtain vector elements**/	
		for (p=frstEl; p< frstEl+count; p++)
		{	
			 int optnElmts=0;
			gotten = gottenvalue.get(p).toString();
			Vector options = new Vector();
			if(gotten.charAt(i)==',')
			{
			c=gotten.charAt(i);
			prev = gotten.charAt(i-1);	
			
			if(c == ',' && (prev== '0'||prev == '1'||prev == '2'||prev == '3'||prev == '4'||prev == '5'||prev == '6'||prev =='7'|| prev == '8' || prev == '9'))
			{
		/**obtaining option control values**/
				for(j=7;j< gotten.length(); j++)
				{
					d=gotten.charAt(j);
					if(d != '"' && d != '/' && d != '>' && d != ',')
					{
						options.add(String.valueOf(d));
						optnElmts++;
					
					}
				}
				val2change.add(gotten);
				optnElments.add(Integer.toString(optnElmts));
				ValOptions.add(options);	
			}
			}
			
		}
		
	}
	
	public void setnumofValues(int val)
	{
		numOfValues = val;
	}
	
	public int getnumofValues()
	{
		return numOfValues;
	}
	
	private String correctFile()
	{
		String val2chng, optionval ;
		int fstElm, lineIndx;
		int optelmt, numOfval=0;
		int counted;
		System.out.println("multselect valuedata items to change are: "+ val2change.toString()  );
		System.out.println("multselect items total number to change for each is: "+ optnElments.toString()  );
		System.out.println("multselect item values in each is: "+ ValOptions.toString()  );
			
			
		/** loop counting the values to change**/
		while(numOfval < val2change.size())
		{
		int counts=0;
		int num=0;
		
		if(vc.contains(val2change.get(numOfval)))
		{
			lineIndx= vc.indexOf(val2change.get(numOfval));
		
			
		/**Number of elements in the first index returned**/	
			counted = Integer.parseInt((optnElments.get(optnElments.indexOf(optnElments.firstElement())+numOfval).toString()));
			
			optionval= ValOptions.get(numOfval).toString();
			System.out.println(" get them right"+ optionval);
			
		/** loop counting how many times to insert changes**/
			while(counts <= counted-1)
			{
				char look;
				
				
				
		/** loop for determining the option values with in the identified value item**/				
				
				while(num <optionval.length())
				{
				
					look= optionval.charAt(num);
					if( look == '0'|| look == '1'|| look == '2' || look == '3'|| look == '4'|| look == '5' || look == '6'|| look == '7'||look == '8'||look == '9')
					{
						
						if(counts <= 0)
						{
						vc.setElementAt(rplaceVal+look+"\"/>", lineIndx);
						counts++;
						}
						else
						{                                         
						vc.insertElementAt( vc.get(lineIndx-2).toString()+" " + vc.get(lineIndx-1).toString()+" "+ rplaceVal + look +"\"/>", lineIndx+counts);
						counts++;
						}
					}
					
					num++;
				
				}
				
						
			
			}
		}else
		{
			System.out.println("not there");
		}
		
		
			numOfval++;
		}
		String contents ="";
		int counter=0;
		while(counter <= vc.indexOf(vc.lastElement()))
		{
		 contents= contents + vc.get(counter).toString()+ " "; 
		 counter++;
		}
			System.out.println(contents);
			return contents;
	}
	

}
