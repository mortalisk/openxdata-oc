package org.omevac.openclinica.convert;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.omevac.openclinica.CrfMetaData;
import org.omevac.openclinica.ItemMetaData;
import org.omevac.openclinica.db.OdmXformDAO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Converts an ODM xml document to XForms.
 * 
 * @author daniel
 *
 */
public class OdmXform {
	
	private static final String NODE_NAME_ITEMDATA = "ItemData";
	private static final String NODE_NAME_FORMDATA = "FormData";
	private static final String NODE_ATTRIBUTE_ITEMOID = "ItemOID";
	private static final String NODE_ATTRIBUTE_FORMOID = "FormOID";
	private static final String NODE_ATTRIBUTE_VALUE = "Value";
	
	/** Namespace prefix for XForms. */
	private static final String PREFIX_XFORMS = "xf";
	
	private static final String PREFIX_XFORMS_AND_COLON = "xf:";
	
	/** Namespace prefix for XForms events. */
	private static final String PREFIX_XFORM_EVENTS = "ev";
	
	/** Namespace prefix for XML schema. */
	private static final String PREFIX_XML_SCHEMA = "xsd";
	
	/** The second Namespace prefix for XML schema. */
	private static final String PREFIX_XML_SCHEMA2 = "xs";
	
	/** Namespace prefix for XML schema instance. */
	private static final String PREFIX_XML_INSTANCES = "xsi";
	
	/** Namespace for XForms. */
	private static final String NAMESPACE_XFORMS = "http://www.w3.org/2002/xforms";
	
	/** Namespace for XForm events. */
	private static final String NAMESPACE_XFORM_EVENTS = "http://www.w3.org/2001/xml-events";
	
	/** Namespace for XML schema. */
	private static final String NAMESPACE_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	/** Namespace for XML schema instance. */
	private static final String NAMESPACE_XML_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	
	
	private static final String NODE_NAME_XFORMS = PREFIX_XFORMS_AND_COLON+"xforms";
	private static final String NODE_NAME_INSTANCE = PREFIX_XFORMS_AND_COLON+"instance";
	private static final String NODE_NAME_MODEL = PREFIX_XFORMS_AND_COLON+"model";
	private static final String NODE_NAME_BIND = PREFIX_XFORMS_AND_COLON+"bind";
	private static final String NODE_NAME_LABEL = PREFIX_XFORMS_AND_COLON+"label";
	private static final String NODE_NAME_HINT = PREFIX_XFORMS_AND_COLON+"hint";
	private static final String NODE_NAME_ITEM = PREFIX_XFORMS_AND_COLON+"item";
	private static final String NODE_NAME_INPUT = PREFIX_XFORMS_AND_COLON+"input";
	private static final String NODE_NAME_SELECT = PREFIX_XFORMS_AND_COLON+"select";
	private static final String NODE_NAME_SELECT1 = PREFIX_XFORMS_AND_COLON+"select1";
	private static final String NODE_NAME_REPEAT = PREFIX_XFORMS_AND_COLON+"repeat";
	private static final String NODE_NAME_TRIGGER = PREFIX_XFORMS_AND_COLON+"trigger";
	private static final String NODE_NAME_SUBMIT = PREFIX_XFORMS_AND_COLON+"submit";
	private static final String NODE_NAME_VALUE = PREFIX_XFORMS_AND_COLON+"value";
	private static final String NODE_NAME_GROUP = PREFIX_XFORMS_AND_COLON+"group";
	
	
	private static final String ATTRIBUTE_NAME_ID = "id";
	private static final String ATTRIBUTE_NAME_BIND = "bind";
	private static final String ATTRIBUTE_NAME_REF = "ref";
	private static final String ATTRIBUTE_NAME_NODESET = "nodeset";
	private static final String ATTRIBUTE_NAME_APPEARANCE = "appearance";
	private static final String ATTRIBUTE_NAME_MULTIPLE = "multiple";
	private static final String ATTRIBUTE_NAME_READONLY= "readonly";
	private static final String ATTRIBUTE_NAME_REQUIRED = "required";
	private static final String ATTRIBUTE_NAME_TYPE = "type";
	//private static final String ATTRIBUTE_NAME_PAGE_NO = "page_no";
	private static final String ATTRIBUTE_NAME_NAME = "name";
	
	
	private static final String DATA_TYPE_DATE = "xsd:date";
	private static final String DATA_TYPE_INT = "xsd:int";
	private static final String DATA_TYPE_TEXT = "xsd:string";
	private static final String DATA_TYPE_BOOLEAN = "xsd:boolean";
	
	private static final String MODEL_ID = "openclinica_model";
	private static final String INSTANCE_ID = "openclinica_model_instance";
	
	private static final int SELECT1_RESPONSE_TYPE_ID = 6;
	private static final int SELECT_RESPONSE_TYPE_ID = 7;
	
	private static final String XPATH_VALUE_TRUE = "true()";
	
	private static final String ATTRIBUTE_NAME_STUDY_OID = "StudyOID";
	private static final String ATTRIBUTE_NAME_SUBJECT_KEY = "SubjectKey";
	private static final String ATTRIBUTE_NAME_STUDY_EVENT_OID = "StudyEventOID";
	private static final String ATTRIBUTE_NAME_FORM_OID = "FormOID";
	
	private static final String NODE_NAME_CLINICAL_DATA = "ClinicalData";
	
	
	private OdmXformDAO dao;
	
	/**
	 * Creates a new ODM to XForms object.
	 *
	 */
	public OdmXform(){
		dao = new OdmXformDAO();
	}
	
	/**
	 * Converts an ODM xml string to xforms.
	 * 
	 * @param odm the odm xml.
	 * @return the xforms xml.
	 */
	public String fromOdm2Xform(String odmXml){
		String xform = null;
		
		try{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		    
		    //Create xforms document and add xforms node.
		    Document xformDoc = builder.newDocument();
		    Element xformsNode = xformDoc.createElement(NODE_NAME_XFORMS);
		    xformsNode.setAttribute("xmlns:"+PREFIX_XFORMS, NAMESPACE_XFORMS);
			xformDoc.appendChild( xformsNode);
			
			//add model node.
			Element modelNode =  xformDoc.createElement(NODE_NAME_MODEL);
			modelNode.setAttribute(ATTRIBUTE_NAME_ID, MODEL_ID);
			xformsNode.appendChild(modelNode);
			
			//add instance node.
			Element instanceNode =  xformDoc.createElement(NODE_NAME_INSTANCE);
			instanceNode.setAttribute(ATTRIBUTE_NAME_ID, INSTANCE_ID);
			modelNode.appendChild(instanceNode);
			
			//Create the ODM doc.
			Document odmDoc = builder.parse(new InputSource(new StringReader(odmXml)));
			CrfMetaData crfMetaData = dao.getCrfMetaData(odmDoc.getElementsByTagName(NODE_NAME_FORMDATA).item(0).getAttributes().getNamedItem(ATTRIBUTE_NAME_FORM_OID).getTextContent());
			odmDoc.getDocumentElement().setAttribute(ATTRIBUTE_NAME_NAME,crfMetaData.getName());
			odmDoc.getDocumentElement().setAttribute(ATTRIBUTE_NAME_ID,String.valueOf(crfMetaData.getVersionId()));
			
			//start building xforms ui controls for all itemdata nodes.
			buildUIControls(xformDoc,odmDoc,xformDoc.getDocumentElement(),modelNode);
			
			//copy the ODM doc root node as a child of the xforms instance node.
		    Node instanceDataNode = odmDoc.getDocumentElement().cloneNode(true);
			xformDoc.adoptNode(instanceDataNode);
			instanceNode.appendChild(instanceDataNode);
			
		    xform = doc2String(xformDoc);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return xform;
	}
	
	/**
	 * 
	 * @param xformDoc
	 * @param odmDoc
	 * @param xformRootNode
	 * @param modelNode
	 */
	private void buildUIControls(Document xformDoc, Document odmDoc, Element xformRootNode, Element modelNode){
		
		//Get all itemdata nodes.
		NodeList dataItemNodes = odmDoc.getElementsByTagName(NODE_NAME_ITEMDATA);
		
		Element groupNode = null; String pageName = null;
		String instanceRootPath = "";
		
		//Create binding and ui control nodes for each itemData node.
	    int nodeCount = dataItemNodes.getLength();
	    for(int currentNodeIndex = 0; currentNodeIndex < nodeCount; currentNodeIndex++){
	    	
	    	//Get current node and then its metadata.
	    	Node itemDataNode = dataItemNodes.item(currentNodeIndex);
	    	String itemOid = getNodeAttributeValue(itemDataNode,NODE_ATTRIBUTE_ITEMOID);
	    	ItemMetaData metaData = dao.getItemMetaData(itemOid);
	    	
	    	//Create bind node.
	    	Element bindNode = xformDoc.createElement(NODE_NAME_BIND);
	    	bindNode.setAttribute(ATTRIBUTE_NAME_ID, itemOid);
	    	bindNode.setAttribute(ATTRIBUTE_NAME_TYPE, getXmlDataType(metaData.getItemDataTypeId()));
	    	
	    	if(metaData.isRequired())
	    		bindNode.setAttribute(ATTRIBUTE_NAME_REQUIRED, XPATH_VALUE_TRUE);
	    	
	    	if(instanceRootPath.length() == 0)
	    		instanceRootPath = getNodeInstanceRootPath(itemDataNode,NODE_NAME_CLINICAL_DATA);
	    	
	    	bindNode.setAttribute(ATTRIBUTE_NAME_NODESET, instanceRootPath+"/ClinicalData/SubjectData/StudyEventData/FormData/ItemGroupData/ItemData[@ItemOID='"+itemOid+"']/@Value");
	    	modelNode.appendChild(bindNode);
	    	
	    	//Create a group for each section
	    	if(currentNodeIndex == 0){
	    		groupNode = createGroupNode(metaData,xformDoc);
	    		pageName = metaData.getPageTitle();
	    	}
	    	else if(!pageName.equals(metaData.getPageTitle())){
	    		groupNode = createGroupNode(metaData,xformDoc);
	    		pageName = metaData.getPageTitle();
	    	}
	    		
	    	
	    	//Create the xforms ui control for the current itemData node.
	    	createUIControl(xformDoc,groupNode,metaData,itemOid);
	    	
	    	//Remove the prefilled itemData value.
	    	itemDataNode.getAttributes().getNamedItem(NODE_ATTRIBUTE_VALUE).setTextContent(metaData.getDefaultValue());
	    }
	    
	    createDefaultUIControls(xformDoc,modelNode,groupNode,instanceRootPath);
	}
	
	private String getNodeInstanceRootPath(Node node, String fromNodeName){
		String path = "";
		
		boolean startAppendingPath = false;
		Node parent = node.getParentNode();
		while(parent != null){
			if(parent.getNodeName().equalsIgnoreCase("instance"))
				break;
			else if(parent.getNodeName().equalsIgnoreCase(fromNodeName))
				startAppendingPath = true;
			else if(startAppendingPath){
				if(parent.getNodeType() == Node.ELEMENT_NODE)
					path += "/" + parent.getNodeName();
			}
			
			parent = parent.getParentNode();
		}
		
		return path;
	}
	
	private Element createGroupNode(ItemMetaData metaData, Document xformDoc){
		String pageName = metaData.getPageTitle();
		Element groupNode = xformDoc.createElement(NODE_NAME_GROUP);
		Element labelNode = xformDoc.createElement(NODE_NAME_LABEL);
		labelNode.setTextContent(pageName);
		groupNode.appendChild(labelNode);
		xformDoc.getDocumentElement().appendChild(groupNode);
		return groupNode;
	}
	
	/**
	 * Create the default ui controls.
	 * 
	 * @param xformDoc
	 */
	private void createDefaultUIControls(Document xformDoc, Element modelNode, Element groupNode, String instanceRootPath){
    	//Create the binding for Study OID.
    	/*Element bindNode = xformDoc.createElement(NODE_NAME_BIND);
    	bindNode.setAttribute(ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_STUDY_OID);
    	bindNode.setAttribute(ATTRIBUTE_NAME_TYPE, DATA_TYPE_TEXT);
    	bindNode.setAttribute(ATTRIBUTE_NAME_REQUIRED, XPATH_VALUE_TRUE);
    	bindNode.setAttribute(ATTRIBUTE_NAME_NODESET, "/ODM/ClinicalData/@"+ATTRIBUTE_NAME_STUDY_OID);
    	modelNode.appendChild(bindNode);*/
    	
    	//Create the binding for Subject OID.
		Element bindNode = xformDoc.createElement(NODE_NAME_BIND);
    	bindNode.setAttribute(ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_SUBJECT_KEY);
    	bindNode.setAttribute(ATTRIBUTE_NAME_TYPE, DATA_TYPE_TEXT);
    	bindNode.setAttribute(ATTRIBUTE_NAME_REQUIRED, XPATH_VALUE_TRUE);
    	bindNode.setAttribute(ATTRIBUTE_NAME_NODESET, instanceRootPath+"/ClinicalData/SubjectData/@"+ATTRIBUTE_NAME_SUBJECT_KEY);
    	modelNode.appendChild(bindNode);
    	
       	//Create the binding for Study Event OID.    	
    	/*bindNode = xformDoc.createElement(NODE_NAME_BIND);
    	bindNode.setAttribute(ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_STUDY_EVENT_OID);
    	bindNode.setAttribute(ATTRIBUTE_NAME_TYPE, DATA_TYPE_TEXT);
    	bindNode.setAttribute(ATTRIBUTE_NAME_REQUIRED, XPATH_VALUE_TRUE);
    	bindNode.setAttribute(ATTRIBUTE_NAME_NODESET, "/ODM/ClinicalData/SubjectData/StudyEventData/@"+ATTRIBUTE_NAME_STUDY_EVENT_OID);
    	modelNode.appendChild(bindNode);*/

    	
    	//Create the actual ui control for Study OID 
		/*Element uiControlNode = xformDoc.createElement(NODE_NAME_INPUT);
    	uiControlNode.setAttribute(ATTRIBUTE_NAME_BIND,ATTRIBUTE_NAME_STUDY_OID);
    	//uiControlNode.setAttribute(ATTRIBUTE_NAME_PAGE_NO,"1");
    	groupNode.appendChild(uiControlNode);
    	
    	Element labelNode = xformDoc.createElement(NODE_NAME_LABEL);
    	labelNode.setTextContent("Study OID");
    	uiControlNode.appendChild(labelNode);*/
    	
    	//Create the actual ui control for Subject OID
    	Element uiControlNode = xformDoc.createElement(NODE_NAME_INPUT);
    	uiControlNode.setAttribute(ATTRIBUTE_NAME_BIND,ATTRIBUTE_NAME_SUBJECT_KEY);
    	//uiControlNode.setAttribute(ATTRIBUTE_NAME_PAGE_NO,"1");
    	groupNode.appendChild(uiControlNode);
    	
    	Element labelNode = xformDoc.createElement(NODE_NAME_LABEL);
    	labelNode.setTextContent("Subject OID");
    	uiControlNode.appendChild(labelNode);
    	
    	//Create the actual ui control for Study Event OID
    	/*uiControlNode = xformDoc.createElement(NODE_NAME_INPUT);
    	uiControlNode.setAttribute(ATTRIBUTE_NAME_BIND,ATTRIBUTE_NAME_STUDY_EVENT_OID);
    	//uiControlNode.setAttribute(ATTRIBUTE_NAME_PAGE_NO,"1");
    	groupNode.appendChild(uiControlNode);*/
    	
    	/*labelNode = xformDoc.createElement(NODE_NAME_LABEL);
    	labelNode.setTextContent("Study Event OID");
    	uiControlNode.appendChild(labelNode);*/
	}
	
	/**
	 * 
	 * @param xformDoc
	 * @param xformRootNode
	 * @param metaData
	 * @param itemOid
	 */
	private void createUIControl(Document xformDoc, Element xformRootNode, ItemMetaData metaData, String itemOid){
    	
		//Determine the type of ui control
		int responseTypeId = metaData.getResponseTypeId();
    	String nodeName = NODE_NAME_INPUT;
    	if(responseTypeId == SELECT1_RESPONSE_TYPE_ID)
    		nodeName = NODE_NAME_SELECT1;
    	else if(responseTypeId == SELECT_RESPONSE_TYPE_ID)
    		nodeName = NODE_NAME_SELECT;
    	
    	//Create ui control node and add it to the xforms doc.
    	Element uiControlNode = xformDoc.createElement(nodeName);
    	uiControlNode.setAttribute(ATTRIBUTE_NAME_BIND,itemOid);
    	//uiControlNode.setAttribute(ATTRIBUTE_NAME_PAGE_NO,String.valueOf(metaData.getPageNo()));
    	xformRootNode.appendChild(uiControlNode);
    	
    	//Create label node and add it to the ui control node.
    	Element labelNode = xformDoc.createElement(NODE_NAME_LABEL);
    	labelNode.setTextContent(metaData.getLeftItemText());
    	uiControlNode.appendChild(labelNode);
    	
    	//add hint if available
    	if(metaData.getRegExpErrorMsg() != null && metaData.getRegExpErrorMsg().trim().length() > 0){
    		Element hintNode = xformDoc.createElement(NODE_NAME_LABEL);
    		hintNode.setTextContent(metaData.getRegExpErrorMsg());
	    	uiControlNode.appendChild(hintNode);
    	}
    	
    	//Create items for select and select1
    	if(responseTypeId == SELECT1_RESPONSE_TYPE_ID || responseTypeId == SELECT_RESPONSE_TYPE_ID)
    		createSelectItems(xformDoc,metaData,uiControlNode,itemOid);		
	}
	
	/**
	 * 
	 * @param xformDoc
	 * @param metaData
	 * @param uiControlNode
	 * @param itemOid
	 */
	private void createSelectItems(Document xformDoc, ItemMetaData metaData, Element uiControlNode, String itemOid){
    	String[] labels = metaData.getOptionsText().split(",");
    	String[] vals = metaData.getOptionsValues().split(",");
    	
    	if(labels.length == vals.length){
	    	for(int i=0; i<labels.length; i++){
	    		Element itemNode = xformDoc.createElement(NODE_NAME_ITEM);
	    		
	    		Element labelNode = xformDoc.createElement(NODE_NAME_LABEL);
	    		labelNode.setTextContent(labels[i]);
	    		itemNode.appendChild(labelNode);
		    	
		    	Element valueNode = xformDoc.createElement(NODE_NAME_VALUE);
		    	valueNode.setTextContent(vals[i]);
		    	itemNode.appendChild(valueNode);
		    	
		    	uiControlNode.appendChild(itemNode);
	    	}
    	}
    	else
    		System.out.println(itemOid + " mismatch between options_text and options_values");		
	}
	
	/**
	 * Gets an xml data type from an openclinica data typeid
	 * 
	 * @param dataTypeId the openclinica data typeid
	 * @return the xml data type
	 */
	private String getXmlDataType(int dataTypeId){
		String type = DATA_TYPE_TEXT; //5
		switch(dataTypeId){
		case 1:
			type = DATA_TYPE_BOOLEAN;
			break;
		case 6:
			type =  DATA_TYPE_INT;
			break;
		case 9:
			type =  DATA_TYPE_DATE;
			break;
		}
		
		return type;
	}
	
	private String getNodeAttributeValue(Node node, String attributeName){
		return node.getAttributes().getNamedItem(attributeName).getTextContent();
	}
	
	/**
	 * Converts a document to its text representation.
	 * 
	 * @param doc - the document.
	 * @return - the text representation of the document.
	 */
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
}
