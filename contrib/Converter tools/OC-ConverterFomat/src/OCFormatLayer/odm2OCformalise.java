package OCFormatLayer;

import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.Comment;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.util.StringTokenizer;
import java.util.Vector;

/** 
 * Converts an ODM xml document from wrong format to the right format accepted by Openclinica .
 * @author gbro
 *
 */

public class odm2OCformalise { 
	
	private static final String ItemGrpdata = "ItemGroupData";
	private static final String Itemdata = "ItemData";
	private static final String ItemGrpOID = "ItemGroupOID";
	private static final String ItemOID = "ItemOID";
	private static final String valuedata = "Value";
	private static final int maxOptnValues =9;
	private int STATE =0;
	
public odm2OCformalise()
{
	
}
/**
 * method takes File ODMfile and returns its correct string format
 *  **/
public String OCTrueformat(File str)
{
	String rightStr = null;
	String ODMstr= null;
	ODMstr = ODMStringData(str);
	
	try
	{
	 DocumentBuilder builds = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				//create the document 
	 	Document Doc = builds.parse(new InputSource(new StringReader(ODMstr)));
		checkItemGrpData(Doc);
		rightStr = checkItemData(Doc);
		if(rightStr.equals(null))
		{
			throw new Exception("The File Cannot be null"); 
		}else
		{
			setStateto(1);
		}
		
	}	
	catch(ParserConfigurationException p)
	{
		p.printStackTrace();
	}
	catch(SAXException x)
	{
		x.printStackTrace(); 
	}
	catch(IOException e)
	{
		e.printStackTrace();
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
	}
 return rightStr;
}
/** 
 *
 * method for determining the ItemGroupOID
 * **/	
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
		
	}
	
	return TrueOID;
}
/** 
 * method for checking itemGroupData
 * **/
public void checkItemGrpData(Document OCDataDoc)
{
	if(OCDataDoc != null)
	{
	NodeList chldnodes = OCDataDoc.getDocumentElement().getElementsByTagName(ItemGrpdata);
		
		if(chldnodes != null)
		{
	
		int numofnodes = 0;
		Node needednode = null;
		Element grpAttr = null;
		String itemGrpAttr = null;
		
			while(numofnodes < chldnodes.getLength())
			{	
				
				needednode = chldnodes.item(numofnodes);
				if(needednode != null || !(needednode instanceof Text || needednode instanceof Comment))
				{
					grpAttr = (Element)needednode;
					itemGrpAttr = grpAttr.getAttribute(ItemGrpOID);
					grpAttr.setAttribute(ItemGrpOID,getTrueOID(itemGrpAttr));
					itemGrpAttr = grpAttr.getAttribute(ItemGrpOID);
					
				}
				numofnodes++;
			}
		
		}
	
	}
}
/**
 * checking for itemdata elements in the documents
 * 
 **/
public String checkItemData(Document OCDataDoc)
{
	String TrueStr =null;
	if(OCDataDoc != null)
	{
	NodeList ItemDatanodes = OCDataDoc.getDocumentElement().getElementsByTagName(Itemdata);
	
		if(ItemDatanodes != null)
		{
			int numofnodes = 0;
			Node needednode = null;
			Element DataAttr = null;
			String itemDataAttrVal  = null;
		
			while(numofnodes < ItemDatanodes.getLength())
			{	
				
				needednode = ItemDatanodes.item(numofnodes);
				if(needednode != null || !(needednode instanceof Text || needednode instanceof Comment))
				{
					DataAttr = (Element)needednode;
					itemDataAttrVal = DataAttr.getAttribute(valuedata);
			
					if(itemDataAttrVal.contains(" "))
					{
						AddItemDataValues(checkForValues(itemDataAttrVal),needednode);
					}
				}
				numofnodes++;
			}
		TrueStr = doc2String(OCDataDoc);
		
		}
	
	}
	
	return TrueStr;
}
/**
 * Method takes the Vector containing the option values, and the Node, from which options were obtained
 * 
 * **/
public void AddItemDataValues( Vector itemDAttrVal,Node oldnode )
{
	
	int numOfoptns =0;
	while(numOfoptns < itemDAttrVal.size())
	{
		
			Element node = (Element)oldnode.cloneNode(true);
			node.setAttribute(valuedata, itemDAttrVal.elementAt(numOfoptns).toString());
			oldnode.getParentNode().insertBefore(node, oldnode);
			
			numOfoptns++;
	}
	oldnode.getParentNode().removeChild(oldnode);
	
}
/**
 * Methods takes identified value options breaks them down into tokens and stores them to a vector
 * 
 * **/
public Vector checkForValues(String Vals)
{
	Vector options = new Vector();
	StringTokenizer tokens = new StringTokenizer(Vals);
	
	while(tokens.hasMoreTokens())
	{
		String check =tokens.nextToken();
		
		for(int j=0; j<= maxOptnValues; j++)
		{
		if(check.equals(String.valueOf(j)))
		{
			options.add(check);
		}
		}		
	}
	
	//System.out.println(options.toString());
	return options;
}
/**
 *Method takes an ODM file and converts it to string format
 *
 ***/
public String ODMStringData(File str)
{
	String sttemp = null;
	int lastBytPos =0;
	int AvlByt = 0;
	
	try
	{
	BufferedInputStream Binput = new BufferedInputStream(new FileInputStream(str));
	
	AvlByt = Binput.available();
	byte[] buffer = new byte[AvlByt];
		while(AvlByt != 0)
		{
			Binput.read(buffer,lastBytPos, AvlByt);
			lastBytPos+=AvlByt;
			AvlByt =Binput.available();
		}	
		 sttemp = new String(buffer);
	}catch(IOException e)
	{
		e.printStackTrace();
	}


return sttemp;
}
/**
 * method takes Documents and returns its string
 * 
 * **/
private String doc2String(Document doc){
	try{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		StringWriter outStream  = new StringWriter();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(outStream);
		transformer.transform(source, result);
		return outStream.getBuffer().toString();
	}
	catch(Exception e){
		e.printStackTrace();
	}
	
	return null;
}
public void  setStateto(int state)
{
	STATE= state;
}
public int gateState()
{
	return STATE;
}

}
