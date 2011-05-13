package org.openxdata.server.serializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

import minixpath.XPathExpression;

import org.apache.commons.codec.binary.Base64;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.openxdata.model.Condition;
import org.openxdata.model.DynamicOptionDef;
import org.openxdata.model.FormData;
import org.openxdata.model.FormDef;
import org.openxdata.model.OpenXdataConstants;
import org.openxdata.model.OptionDef;
import org.openxdata.model.PageData;
import org.openxdata.model.PageDef;
import org.openxdata.model.QuestionData;
import org.openxdata.model.QuestionDef;
import org.openxdata.model.RepeatQtnsData;
import org.openxdata.model.RepeatQtnsDataList;
import org.openxdata.model.RepeatQtnsDef;
import org.openxdata.model.SkipRule;
import org.openxdata.model.ValidationRule;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Provides conversion from xform to epihandy object model and vice vasa.
 * 
 * @author Daniel Kayiwa
 *
 */
public class KxmlSerializerUtil{

	public static final String ATTRIBUTE_VALUE_ENABLE = "enable";
	public static final String ATTRIBUTE_VALUE_DISABLE = "disable";
	public static final String ATTRIBUTE_VALUE_SHOW = "show";
	public static final String ATTRIBUTE_VALUE_HIDE = "hide";

	private static byte nextOptionId = -128;
	private static byte nextQuestionId = -128;

	/**
	 * Default Constructor
	 *
	 */
	public KxmlSerializerUtil(){

	}

	private static byte getNextOptionId() {
		return nextOptionId++;
	}

	private static byte getNextQuestionId() {
		return nextQuestionId++;
	}

	private static void initIds(){
		nextOptionId = -128;
		nextQuestionId = -128;
	}

	private static boolean reachedMaxQuestionId(){
		return nextQuestionId > 127;
	}

	/**
	 * Updates the XForm model with the answers.
	 * 
	 * @param doc -  the XForm document having the model.
	 * @param formData - the form data having the answers.
	 * @return - a string representing the xml of the updated model only.
	 */
	public static String updateXformModel(Document doc, FormData formData) {
		for(int pageNo=0; pageNo<formData.getPages().size(); pageNo++){
			PageData page = (PageData)formData.getPages().elementAt(pageNo);
			Vector<?> questions = page.getQuestions();
			if(questions != null){
				for(int i=0; i<questions.size(); i++)
					updateModel(doc,(QuestionData)questions.elementAt(i),formData.getDef());
			}
		}

		Element instanceDataNode = getInstanceDataNode(doc);
		return fromDoc2String(getDocumentFromNode(instanceDataNode));
	}

	public static Document getDocumentFromNode(Element element){
		Document doc = new Document();
		doc.setEncoding("UTF-8");

		doc.addChild(org.kxml2.kdom.Element.ELEMENT, element);

		element.setPrefix("xf", "http://www.w3.org/2002/xforms");
		element.setPrefix("xsd", "http://www.w3.org/2001/XMLSchema");
		element.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		return doc;
	}

	/**
	 * Fills an xforms document model with answers from a question.
	 * 
	 * @param doc
	 * @param qtnData
	 */
	private static void updateModel(Document doc, QuestionData qtnData, FormDef formDef) {
		//we dot spit out answers for invisible and disabled questions since
		//they are considered non-relevant. (Is this really correct?????)
		if(true){		
			String value = qtnData.getValueAnswer();

			if (value != null) {
				if(qtnData.getDef().getType() == QuestionDef.QTN_TYPE_AUDIO ||
						qtnData.getDef().getType() == QuestionDef.QTN_TYPE_IMAGE ||
						qtnData.getDef().getType() == QuestionDef.QTN_TYPE_VIDEO){

					value = new String(Base64.encodeBase64((byte[])qtnData.getAnswer()));					
				}

				Element elem = getInstanceNode(doc);
				String xpath = qtnData.getDef().getVariableName();

				if(qtnData.getDef().getType() != QuestionDef.QTN_TYPE_REPEAT){
					xpath = new String(xpath.toCharArray(), 1, xpath.length()-1);
					int pos = xpath.lastIndexOf('@'); String attributeName = null;
					if(pos > 0){
						attributeName = xpath.substring(pos+1,xpath.length());
						xpath = xpath.substring(0,pos-1);
					}
					XPathExpression xpls = new XPathExpression(elem, xpath);
					Vector<?> result = xpls.getResult();

					for (Enumeration<?> e = result.elements(); e.hasMoreElements();) {
						Object obj = e.nextElement();
						if (obj instanceof Element){
							if(pos > 0) //Check if we are to set attribute value.
								((Element) obj).setAttribute(null, attributeName, value);
							else{
								removeTextNode((Element)obj);
								((Element) obj).addChild(Node.TEXT, value);
							}

							break;
						}
					}
				}
				else
					updateRepeatModel(elem,qtnData,formDef);
			}
		}
	}

	/**
	 * Updates an xforms document with answers from a repeat question.
	 * 
	 * @param doc the xforms document.
	 * @param qtnData the answer data.
	 */
	private static void updateRepeatModel(Element instanceNode, QuestionData qtnData, FormDef formDef) {
		RepeatQtnsDataList repeatQtnsDataList = (RepeatQtnsDataList)qtnData.getAnswer();
		if(repeatQtnsDataList == null)
			return; //Not a single repeat row filled.

		String xpath = qtnData.getDef().getVariableName();

		String formVarName = formDef.getVariableName();
		if(!xpath.startsWith(formVarName))
			formVarName = "/" + formVarName;
		formVarName += "/";

		boolean nested = xpath.substring(formVarName.length()).contains("/");

		//Element elem = getInstanceNode(doc);
		xpath = new String(xpath.toCharArray(), 1, xpath.length()-1);
		XPathExpression xpls = new XPathExpression(instanceNode, xpath);
		Vector<?> result = xpls.getResult();

		for (Enumeration<?> e = result.elements(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof Element){
				Element elem = (Element) obj;
				Element parent = (Element)((Element) obj).getParent();
				for(int i=0; i<repeatQtnsDataList.size(); i++){
					if(i > 0){
						elem = copyNode((Element) obj);
						removeChildTextNodes(elem);
						parent.addChild(Element.ELEMENT, elem);
					}
					updateRepeatModel(repeatQtnsDataList.getRepeatQtnsData(i),elem,instanceNode,i,nested);
				}

				break;
			}
		}
	}

	private static void removeChildTextNodes(Element node){
		for(int i = 0; i < node.getChildCount(); i++){
			Object child = node.getChild(i);
			if(child instanceof Element)
				removeTextNode((Element)child);
		}
	}

	/**
	 * Updates an xforms document repeat model element with answers.
	 * 
	 * @param doc the xforms document.
	 * @param repeatQtnsDataList the repeat answers.
	 * @param parentNode the model repeat element.
	 */
	private static void updateRepeatModel(RepeatQtnsData repeatQtnsData, Element rptParentNode, Element instanceNode,int index, boolean nested){
		for(int i=0; i<repeatQtnsData.size(); i++){
			QuestionData data = repeatQtnsData.getQuestion(i);
			if(data.getValueAnswer() != null)
				updateRepeatRowModel(data,rptParentNode,instanceNode,index,nested);
		}
	}

	private static void updateRepeatRowModel(QuestionData questionData, Element rptParentNode,Element instanceNode, int index, boolean nested){
		//make multiple copies of this repeat node and fill the with answers in each item in the repeatQtnsDataList
		String xpath = questionData.getDef().getVariableName();

		XPathExpression xpls = new XPathExpression(rptParentNode, xpath);
		Vector<?> result = xpls.getResult();

		if(result.size() > 0){
			Element node = (Element)result.elementAt(0);
			Element newNode = node;

			removeTextNode(newNode); //remove text if any.
			newNode.addChild(Node.TEXT,questionData.getValueAnswer());
		}
	}

	private static void removeTextNode(Element node){
		for(int i=0; i<node.getChildCount(); i++){
			if(node.getType(i) == Element.TEXT){
				String text = node.getText(i).trim();
				if(text.length() > 0 && !text.equalsIgnoreCase("\n")){
					node.removeChild(i);
					return;
				}
			}
		}
	}

	private static Element copyNode(Element node) {
		Document doc = new Document();
		doc.setEncoding("UTF-8");
		Element htmlNode = doc.createElement("http://www.w3.org/2001/XMLSchema-instance", null);
		htmlNode.setName("html");
		htmlNode.setPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		doc.addChild(Element.ELEMENT, htmlNode);
		htmlNode.addChild(Element.ELEMENT, node);

		String xml = KxmlSerializerUtil.fromNode2String(doc.getRootElement()); //doc.getRootElement()

		doc = getDocument(new StringReader(xml));
		Element retNode = doc.getRootElement().getElement(0);

		return retNode;
	}

	private static Element getInstanceNode(Document doc){
		return getInstanceNode(doc.getRootElement());

	}

	private static Element getInstanceDataNode(Document doc){
		return getInstanceDataNode(getInstanceNode(doc));
	}

	private static Element getInstanceNode(Element element){
		int numOfEntries = element.getChildCount();
		for (int i = 0; i < numOfEntries; i++) {
			if (!element.isText(i) && element.getType(i) == Element.ELEMENT) {
				Element child = element.getElement(i);
				String tagname = child.getName();
				if (tagname.equals("instance"))
					return child;
				else{
					child = getInstanceNode(child);
					if(child != null)
						return child;
				}
			}
		}
		return null;
	}

	private static Element getInstanceDataNode(Element element){
		int numOfEntries = element.getChildCount();
		for (int i = 0; i < numOfEntries; i++) {
			if (!element.isText(i) && element.getType(i) == Element.ELEMENT) 
				return element.getElement(i);
		}

		return null;
	}

	public static String fromFormData2XformModel(FormData formData) throws Exception {
		FormDef formDef = formData.getDef();
		Document doc = new Document();
		doc.setEncoding("UTF-8");
		Element rootNode = doc.createElement(null, null);
		rootNode.setName(formDef.getVariableName());
		//rootNode.setAttribute(null, "id", Integer.toString(formDef.getId()));
		doc.addChild(org.kxml2.kdom.Element.ELEMENT, rootNode);

		for(int pageNo=0; pageNo<formData.getPages().size(); pageNo++){
			PageData page = (PageData)formData.getPages().elementAt(pageNo);
			Vector<?> questions = page.getQuestions();
			if(questions != null){
				for(int i=0; i<questions.size(); i++){
					QuestionData qtnData = (QuestionData)questions.elementAt(i);
					//we don't spit out answers for invisible and disabled questions since
					//they are considered non-relevant.
					if(qtnData.getDef().isVisible() && qtnData.getDef().isEnabled()){
						Element node =  doc.createElement(null, null);
						node.setName(qtnData.getDef().getVariableName());
						if(qtnData.getValueAnswer() != null)
							node.addChild(Element.TEXT,qtnData.getValueAnswer());
						rootNode.addChild(Element.ELEMENT,node);
					}
				}	
			}
		}

		return fromDoc2String(doc);
	}	

    public static String fromNode2String(Node node) {
        KXmlSerializer serializer = new KXmlSerializer();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            serializer.setOutput(dos, "UTF-8");
            node.write(serializer);
            serializer.flush();
            return new String(bos.toByteArray(), "UTF-8");
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public static String fromDoc2String(Document doc) {
        KXmlSerializer serializer = new KXmlSerializer();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            serializer.setOutput(dos, "UTF-8");
            doc.write(serializer);
            serializer.flush();
            return new String(bos.toByteArray(), "UTF-8");
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }


	public static FormDef fromXform2FormDef(Reader reader){
		Document doc = getDocument(reader);
		return getFormDef(doc);
	}

    public static Document getDocument(Reader reader) {
        Document doc = new Document();

        try {
            KXmlParser parser = new KXmlParser();
            parser.setInput(reader);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);

            doc.parse(parser);
            return doc;
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        } catch (XmlPullParserException ex) {
            throw new UnexpectedException(ex);
        }
    }

	public static FormDef getFormDef(Document doc){
		initIds();

		Element rootNode = doc.getRootElement();
		Element instanceNode = getInstanceDataNode(doc);
		FormDef formDef = new FormDef();
		Hashtable<String, String> id2VarNameMap = new Hashtable<String, String>();
		Hashtable<QuestionDef, String> relevants = new Hashtable<QuestionDef, String>();
		Hashtable<QuestionDef, String> constraints = new Hashtable<QuestionDef, String>();
		Hashtable<QuestionDef, String> actions = new Hashtable<QuestionDef, String>();
		Hashtable<QuestionDef, String> constraintMsgs = new Hashtable<QuestionDef, String>();
		Vector<QuestionDef> repeats = new Vector<QuestionDef>();
		Hashtable<String, QuestionDef> rptKidMap = new Hashtable<String, QuestionDef>();
		parseElement(formDef,rootNode,id2VarNameMap,null,relevants,actions,constraints,constraintMsgs,repeats,rptKidMap,(int)0,null,instanceNode);
		if(formDef.getName() == null || formDef.getName().length() == 0)
			formDef.setName(formDef.getVariableName());
		setDefaultValues(instanceNode,formDef,id2VarNameMap);
		addSkipRules(formDef,id2VarNameMap,relevants,actions);
		addValidationRules(formDef,id2VarNameMap,constraints,constraintMsgs);
		return formDef;
	}

	private static String getNodeTextValue(Element dataNode,String name){
		Element node = getNode(dataNode,name);
		if(node != null)
			return getTextValue(node);
		return null;
	}

	private static void setDefaultValues(Element dataNode,FormDef formDef,Hashtable<String, String> id2VarNameMap){

		String id, val;
		Iterator<String> keys = id2VarNameMap.keySet().iterator();
		while(keys.hasNext()){
			id = (String)keys.next();
			String variableName = (String)id2VarNameMap.get(id);

			QuestionDef def = formDef.getQuestion(variableName);
			if(def == null)
				continue;

			if(variableName.contains("@"))
				setAttributeDefaultValue(def,variableName,dataNode,formDef);
			else{
				val = getNodeTextValue(dataNode,id);
				if(val == null || val.trim().length() == 0) //we are not allowing empty strings for now.
					continue;

				def.setDefaultValue(val);
			}
		}

		//Now do set default values for repeats since they are not part of the id2VarNameMap
		for(int pageNo=0; pageNo<formDef.getPageCount(); pageNo++){
			PageDef pageDef = formDef.getPageAt(pageNo);
			Vector<?> questions = pageDef.getQuestions();
			if(questions != null){
				for(int qtnNo=0; qtnNo<questions.size(); qtnNo++){
					QuestionDef questionDef = (QuestionDef)questions.elementAt(qtnNo);
					if(questionDef.getType() == QuestionDef.QTN_TYPE_REPEAT)
						;//setQtnsDefaultValues(questionDef.getDataNode(),formDef,questionDef.getRepeatQtnsDef());
				}
			}
		}
	}

	private static void setAttributeDefaultValue(QuestionDef qtn, String variableName,Element dataNode,FormDef formDef){
		String xpath = variableName;

		if(xpath.startsWith(formDef.getVariableName() + "/"))
			xpath = xpath.replace(formDef.getVariableName() + "/", "");
		else if(xpath.startsWith("/"+formDef.getVariableName() + "/"))
			xpath = xpath.replace("/"+formDef.getVariableName() + "/", "");
		else if(xpath.startsWith("/"))
			xpath = new String(xpath.toCharArray(), 1, xpath.length()-1);

		int pos = xpath.lastIndexOf('@'); String attributeName = null;
		if(pos == 0){
			attributeName = variableName.substring(1,variableName.length());
			String value = dataNode.getAttributeValue(null,attributeName);
			if(value != null && value.trim().length() > 0) //we are not allowing empty strings for now.
				qtn.setDefaultValue(value);
			return;
		}

		attributeName = xpath.substring(pos+1,xpath.length());
		xpath = xpath.substring(0,pos-1);

		XPathExpression xpls = new XPathExpression(dataNode, xpath);
		Vector<?> result = xpls.getResult();

		for (Enumeration<?> e = result.elements(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof Element){
				String value = ((Element) obj).getAttributeValue(null,attributeName);
				if(value != null && value.trim().length() > 0){ //we are not allowing empty strings for now.
					qtn.setDefaultValue(value);
					break;
				}
			}
		}
	}

	// returns the first text node of the element or child of the element (recursively)
	private static String getTextValue(Element node){
		int numOfEntries = node.getChildCount();
		for (int i = 0; i < numOfEntries; i++) {
			if (node.isText(i))
				return node.getText(i);

			if(node.getType(i) == Element.ELEMENT){
				String val = getTextValue(node.getElement(i));
				if(val != null)
					return val;
			}
		}

		return null;
	}

	/**
	 * Gets a child element of a parent node with a given name.
	 * 
	 * @param parent - the parent element
	 * @param name - the name of the child.
	 * @return - the child element.
	 */
	private static Element getNode(Element parent, String name){
		for(int i=0; i<parent.getChildCount(); i++){
			if(parent.getType(i) != Element.ELEMENT)
				continue;

			Element child = (Element)parent.getChild(i);
			if(child.getName().equals(name))
				return child;

			child = getNode(child,name);
			if(child != null)
				return child;
		}

		return null;
	}

	public static FormData fromXform2FormData(Reader reader){			
		Document doc = getDocument(reader);		
		FormData formData = new FormData(getFormDef(doc));

		Element dataNode = doc.getRootElement().getElement(0).getElement(1).getElement(0).getElement(0);
		for(int i=0; i<dataNode.getChildCount(); i++){
			Element node = dataNode.getElement(i);
			if(node != null && node.getChildCount() > 0)
				formData.setValue(node.getName(), node.getText(0));
		}

		return formData;
	}

	private static QuestionDef parseElement(FormDef formDef, Element element, Hashtable<String, String> map,QuestionDef questionDef,Hashtable<QuestionDef, String> relevants,Hashtable<QuestionDef, String> actions,Hashtable<QuestionDef, String> constraints,Hashtable<QuestionDef, String> constraintMsgs,Vector<QuestionDef> repeats, Hashtable<String, QuestionDef> rptKidMap, int currentPageNo,QuestionDef parentQtn, Element instanceDataNode){
		String label = ""; //$NON-NLS-1$
		String value = ""; //$NON-NLS-1$

		int numOfEntries = element.getChildCount();
		for (int i = 0; i < numOfEntries; i++) {
			if (!element.isText(i) && element.getType(i) == Element.ELEMENT) {
				Element child = element.getElement(i);
				String tagname = child.getName();

				if(tagname.equals("submit"))
					continue;
				else if (tagname.equals("head"))
					parseElement(formDef,child,map,questionDef,relevants,actions,constraints,constraintMsgs,repeats,rptKidMap,currentPageNo,parentQtn,instanceDataNode);
				else if (tagname.equals("body"))
					parseElement(formDef, child,map,questionDef,relevants,actions,constraints,constraintMsgs,repeats,rptKidMap,currentPageNo,parentQtn,instanceDataNode);
				else if (tagname.equals("title")){
					if(child.getChildCount() > 0 && child.isText(0))
						formDef.setName(getTextContent(child));
				}
				else if (tagname.equals("model"))
					parseElement(formDef, child,map,questionDef,relevants,actions,constraints,constraintMsgs,repeats,rptKidMap,currentPageNo,parentQtn,instanceDataNode);
				else if (tagname.equals("group")){
					String parentName = ((Element)child.getParent()).getName();
					if(!(parentName.equalsIgnoreCase("group"))){
						if(formDef.getPageCount() < ++currentPageNo)
							formDef.addPage();
					}
					parseElement(formDef, child,map,questionDef,relevants,actions,constraints,constraintMsgs,repeats,rptKidMap,currentPageNo,parentQtn,instanceDataNode);
				}
				else if (tagname.equals("instance")) {
					if(formDef.getVariableName() != null && formDef.getVariableName().trim().length() > 0)
						continue; //we only take the first instance node for formdef ref

					Element dataNode = null;
					for(int k=0; k<child.getChildCount(); k++){
						if(!child.isText(k))
							dataNode = (Element)child.getChild(k);
					}
					formDef.setVariableName(dataNode.getName());
					if(dataNode.getAttributeValue(null, "formId") != null)
						formDef.setId(Integer.parseInt(dataNode.getAttributeValue(null, "formId")));
					if(dataNode.getAttributeValue(null, "description-template") != null)
						formDef.setDescriptionTemplate(dataNode.getAttributeValue(null, "description-template"));
					if(dataNode.getAttributeValue(null, "id") != null)
						formDef.setId(Integer.parseInt(dataNode.getAttributeValue(null, "id")));
					if(dataNode.getAttributeValue(null, "name") != null)
						formDef.setName(dataNode.getAttributeValue(null, "name"));
					if(dataNode.getAttributeValue(null, "formKey") != null)
						formDef.setFormKey(dataNode.getAttributeValue(null, "formKey"));
				} else if (tagname.equals("bind") || tagname.equals("ref")) {
					QuestionDef qtn = new QuestionDef();
					//qtn.setId(Byte.parseByte(String.valueOf(i)));
					if(formDef.getPages() == null)
						qtn.setId(getNextQuestionId());
					else{
						if(reachedMaxQuestionId()){
							System.out.println("Failed parsing Xform because it exceeds the currently supported maximum number of questions. Count=255");
							break;
						}
						qtn.setId(getNextQuestionId());
					}
					qtn.setVariableName(child.getAttributeValue(null, "nodeset"));
					setQuestionType(qtn,child.getAttributeValue(null, "type"),child);
					if(child.getAttributeValue(null, "required") != null && child.getAttributeValue(null, "required").equals("true()")){
						if(child.getAttributeValue(null,"action") == null)
							qtn.setMandatory(true);
					}
					if(child.getAttributeValue(null, "readonly") != null && child.getAttributeValue(null, "readonly").equals("true()"))
						qtn.setEnabled(false);
					if(child.getAttributeValue(null, "locked") != null && child.getAttributeValue(null, "locked").equals("true()"))
						qtn.setLocked(true);
					if(child.getAttributeValue(null, "visible") != null && child.getAttributeValue(null, "visible").equals("false()"))
						qtn.setVisible(false);


					if(!addRepeatChildQtn(qtn,repeats,child,map,rptKidMap)){
						if(child.getAttributeValue(null, "id") != null){
							map.put(child.getAttributeValue(null, "id"), qtn.getVariableName());
							formDef.addQuestion(qtn);
						}
						else
							System.out.println("Problem trying to get a missing id attribute"+qtn);
					}

					if(child.getAttributeValue(null, "relevant") != null){
						relevants.put(qtn,child.getAttributeValue(null, "relevant"));
						String action  = child.getAttributeValue(null, "action");
						if(action != null){
							String required = child.getAttributeValue(null, "required");
							if(required != null)
								action += "|" + required;
						}
						actions.put(qtn,action);
					}

					if(child.getAttributeValue(null,"constraint") != null){
						constraints.put(qtn,child.getAttributeValue(null,"constraint"));
						constraintMsgs.put(qtn,child.getAttributeValue(null, "message"));
					}

					if(qtn.getType() == QuestionDef.QTN_TYPE_REPEAT){
						RepeatQtnsDef repeatQtnsDef = new RepeatQtnsDef(qtn);
						qtn.setRepeatQtnsDef(repeatQtnsDef);
						repeats.addElement(qtn);

						questionDef = qtn;
					}

				} else if (tagname.equals("input") || tagname.equals("upload") || tagname.equals("select1") || tagname.equals("select") || tagname.equals("repeat")) {
					String ref = child.getAttributeValue(null, "ref");
					String bind = child.getAttributeValue(null, "bind");
					String varName = (String)map.get(((ref != null) ? ref : bind));

					//new addition may cause bugs
					if(varName == null){
						varName = addNonBindControl(formDef,child,relevants,ref,bind);
						if(ref != null)
							map.put(ref, ref);
					}

					if(reachedMaxQuestionId())
						break;

					if(varName != null){
						QuestionDef qtn = formDef.getQuestion(varName);
						if(qtn == null)
							qtn = (QuestionDef)rptKidMap.get(varName);

						if(tagname.equals("select1") || tagname.equals("select")){
							qtn.setType((tagname.equals("select1")) ? QuestionDef.QTN_TYPE_LIST_EXCLUSIVE : QuestionDef.QTN_TYPE_LIST_MULTIPLE);
							qtn.setOptions(new Vector<Object>());
						}
						else if((tagname.equals("repeat")) && !label.equals("")){
							qtn.setType(QuestionDef.QTN_TYPE_REPEAT);
							qtn.setText(label);
							qtn.setRepeatQtnsDef(new RepeatQtnsDef(qtn));
							formDef.moveQuestion2Page(qtn, currentPageNo);
							label = "";
							int pageNo = currentPageNo;
							if(pageNo == 0) pageNo = 1; //Xform may not have groups for pages.
							parentQtn = qtn;
						}

						//TODO second addition for repeats
						Element parent = (Element)child.getParent(); 
						if(parent.getName().equals("repeat")){
							QuestionDef rptQtnDef = formDef.getQuestion((String)map.get(parent.getAttributeValue(null,"bind") != null ? parent.getAttributeValue(null,"bind") : parent.getAttributeValue(null,"nodeset")));
							rptQtnDef.addRepeatQtnsDef(qtn);
							formDef.removeQuestion(qtn);

							String val = getNodeTextValue(instanceDataNode,qtn.getVariableName());
							if(!(val == null || val.trim().length() == 0))
								qtn.setDefaultValue(val);
						}

						questionDef = qtn;
						parseElement(formDef, child, map,questionDef,relevants,actions,constraints,constraintMsgs,repeats,rptKidMap,currentPageNo,parentQtn,instanceDataNode);
					}
				} else if (tagname.equals("label")){
					String parentName = ((Element)child.getParent()).getName();
					if(parentName.equalsIgnoreCase("input") || parentName.equals("upload") || parentName.equalsIgnoreCase("select") || parentName.equalsIgnoreCase("select1") || parentName.equalsIgnoreCase("item")){
						label = getTextContent(child);
					}
					else if(parentName.equalsIgnoreCase("repeat")){
						if(questionDef != null && child.getChildCount() > 0 && child.isText(0))
							questionDef.setText(getTextContent(child));
					}
					else if(parentName.equalsIgnoreCase("group")){
						label = getTextContent(child);
					}
				}
				else if (tagname.equals("hint")){
					if(questionDef != null && child.getChildCount() > 0 && child.isText(0))
						questionDef.setHelpText(getTextContent(child));
				}
				else if (tagname.equals("item"))
					parseElement(formDef, child,map,questionDef,relevants,actions,constraints,constraintMsgs,repeats,rptKidMap,currentPageNo,parentQtn,instanceDataNode);
				else if (tagname.equals("value")){
					value = getTextContent(child);
				}
				else if(tagname.equals("itemset")){
					questionDef.setType(QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC);
					parseDynamicOptionsList(questionDef,child.getAttributeValue(null,"nodeset"),formDef,(Element)((Element)instanceDataNode.getParent()).getParent());
				}
				else
					parseElement(formDef, child,map,questionDef,relevants,actions,constraints,constraintMsgs,repeats,rptKidMap,currentPageNo,parentQtn,instanceDataNode);
				// TODO - how are other elements like html:p or br handled?
			}
		}

		if (!label.equals("") && !value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			if (questionDef != null && questionDef.getOptions() != null)
				questionDef.addOption(new OptionDef(Byte.parseByte(String.valueOf(questionDef.getOptions().size())),label, value));
		} 
		else if (!label.equals("") && questionDef != null){
			if(questionDef.getText() == null || questionDef.getText().trim().length()==0){
				questionDef.setText(label);
				int pageNo = currentPageNo;
				if(pageNo == 0) pageNo = 1; //Xform may not have groups for pages.
				formDef.moveQuestion2Page(questionDef, pageNo);
			}
			else
				formDef.setPageName(label);
		}

		return questionDef;
	}
	
	// returns all the text content directly within this element
	private static String getTextContent(Element element) {
		StringBuilder text = new StringBuilder();
		for (int i=0, j=element.getChildCount(); i<j; i++) {
			if (element.isText(i)) {
				text.append(element.getText(i));
			}
		}
		return text.toString();
	}

	private static void parseDynamicOptionsList(QuestionDef questionDef, String nodeset, FormDef formDef, Element modelNode){
		if(nodeset == null || nodeset.trim().length() == 0)
			return;

		String binding = getDynamicOptionParentInstanceId(nodeset);
		if(binding == null)
			return;

		binding = "/" + formDef.getVariableName() + "/" + binding;
		QuestionDef parentQuestionDef = formDef.getQuestion(binding);
		if(parentQuestionDef == null)
			return;

		String instanceId = getDynamicOptionChildInstanceId(nodeset);
		if(instanceId == null)
			return;

		Element instanceNode = getInstanceNode(modelNode, instanceId);
		if(instanceNode == null)
			return;

		Hashtable<String, Byte> parentOptionIdMap = new Hashtable<String, Byte>();
		DynamicOptionDef dynamicOptionDef = new DynamicOptionDef();
		dynamicOptionDef.setQuestionId(questionDef.getId());
		int count = instanceNode.getChildCount();
		for(int index = 0; index < count; index++){
			if(instanceNode.getType(index) == Element.ELEMENT){
				Element child = (Element)instanceNode.getChild(index);
				parseDynamicOptions(dynamicOptionDef,questionDef,parentQuestionDef,child,null,parentOptionIdMap,formDef);
				break;
			}
		}

		setDynamicOptionDef(formDef,parentQuestionDef.getId(), dynamicOptionDef);
	}

	private static void parseDynamicOptions(DynamicOptionDef dynamicOptionDef, QuestionDef questionDef, QuestionDef parentQuestionDef, Node node, OptionDef optionDef, Hashtable<String, Byte> parentOptionIdMap, FormDef formDef){
		String label = "";
		String value = "";

		int count = node.getChildCount();
		for(int index = 0; index < count; index++){
			if(node.getType(index) != Node.ELEMENT)
				continue;

			Element child = (Element)node.getChild(index);
			String name = child.getName();
			if(name.equals("item")){
				String parent = ((Element)child).getAttributeValue(null,"parent");
				if(parent == null || parent.trim().length() == 0)
					continue;

				optionDef = new OptionDef(getNextOptionId(),label, value);
				Byte optionId = (Byte)parentOptionIdMap.get(parent);
				if(optionId == null){
					OptionDef optnDef = parentQuestionDef.getOptionWithValue(parent);
					if(parentQuestionDef.getType() == QuestionDef.QTN_TYPE_LIST_EXCLUSIVE_DYNAMIC){
						DynamicOptionDef dynOptionsDef = getChildDynamicOptions(formDef,parentQuestionDef.getId());
						if(dynOptionsDef == null)
							return;
						optnDef = getOptionWithValue(dynOptionsDef,parent);
					}
					if(optnDef == null)
						continue;
					optionId = new Byte(optnDef.getId());
					parentOptionIdMap.put(parent, optionId);
				}
				Vector<OptionDef> optionList = getOptionList(dynamicOptionDef,optionId);
				if(optionList == null){
					optionList = new Vector<OptionDef>();
					setOptionList(dynamicOptionDef,optionId, optionList);
				}
				optionList.add(optionDef);

				parseDynamicOptions(dynamicOptionDef,questionDef,parentQuestionDef,child,optionDef,parentOptionIdMap,formDef);
			}
			else if(name.equals("label")){
				if(child.getChildCount() > 0 && child.isText(0))
					label = getTextContent(child);
			}
			else if(name.equals("value")){
				if(child.getChildCount() > 0 && child.isText(0))
					value = getTextContent(child);
			}
		}

		if (!label.equals("") && !value.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
			if (optionDef != null){
				optionDef.setText(label);
				optionDef.setVariableName(value);
			}
		} 
	}

	@SuppressWarnings("unchecked")
	private static void setOptionList(DynamicOptionDef dynamicOptionDef,Byte optionId, Vector<OptionDef> list){
		if(dynamicOptionDef.getParentToChildOptions() == null)
			dynamicOptionDef.setParentToChildOptions(new Hashtable<Object, Object>());
		dynamicOptionDef.getParentToChildOptions().put(optionId, list);
	}

	private static OptionDef getOptionWithValue(DynamicOptionDef dynamicOptionDef, String value){
		if(dynamicOptionDef.getParentToChildOptions() == null || value == null)
			return null;

		Iterator<?> iterator = dynamicOptionDef.getParentToChildOptions().entrySet().iterator();
		while(iterator.hasNext()){
			OptionDef optionDef = getOptionWithValue((Vector<?>)((Entry<?, ?>)iterator.next()).getValue(),value);
			if(optionDef != null)
				return optionDef;
		}
		return null;
	}

	private static OptionDef getOptionWithValue(Vector<?> options, String value){
		for(int i=0; i<options.size(); i++){
			OptionDef optionDef = (OptionDef)options.get(i);
			if(optionDef.getVariableName().equals(value))
				return optionDef;
		}
		return null;
	}

	private static DynamicOptionDef getChildDynamicOptions(FormDef formDef, byte questionId){
		if(formDef.getDynamicOptions() == null)
			return null;

		Iterator<?> iterator = formDef.getDynamicOptions().entrySet().iterator();
		while(iterator.hasNext()){
			Entry<?, ?> entry = (Entry<?, ?>)iterator.next();
			DynamicOptionDef dynamicOptionDef = (DynamicOptionDef)entry.getValue();
			if(dynamicOptionDef.getQuestionId() == questionId)
				return dynamicOptionDef;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Vector<OptionDef> getOptionList(DynamicOptionDef dynamicOptionDef, Byte optionId){
		if(dynamicOptionDef.getParentToChildOptions() == null)
			return null;
		return (Vector<OptionDef>)dynamicOptionDef.getParentToChildOptions().get(optionId);
	}

	@SuppressWarnings("unchecked")
	private static void setDynamicOptionDef(FormDef formDef, byte questionId, DynamicOptionDef dynamicOptionDef){
		if(formDef.getDynamicOptions() == null)
			formDef.setDynamicOptions(new Hashtable<Object, Object>());
		formDef.getDynamicOptions().put(new Byte(questionId), dynamicOptionDef);
	}

	private static Element getInstanceNode(Element modelNode, String instanceId){
		String xpath = "instance[@id=" + instanceId + "]";
		XPathExpression xpls = new XPathExpression(modelNode, xpath);
		Vector<?> result = xpls.getResult();
		if(result.size() > 0)
			return (Element)result.get(0);
		return null;
	}

	private static String getDynamicOptionChildInstanceId(String nodeset){
		if(nodeset == null)
			return null;

		int pos1 = nodeset.indexOf('\'');
		if(pos1 < 0)
			return null;

		int pos2 = nodeset.indexOf('\'', pos1 + 1);
		if(pos2 < 0 || (pos1 == pos2))
			return null;

		return nodeset.substring(pos1 + 1, pos2);
	}

	private static String getDynamicOptionParentInstanceId(String nodeset){
		if(nodeset == null)
			return null;

		int pos1 = nodeset.lastIndexOf('/');
		if(pos1 < 0)
			return null;

		int pos2 = nodeset.lastIndexOf(']');
		if(pos2 < 0 || (pos1 == pos2))
			return null;

		return nodeset.substring(pos1 + 1, pos2);
	}

	/**
	 * Checks if this is a repeat child question and adds it.
	 * @param qtn the questions to check
	 * @param repeats the list of repeat questions
	 * @return true if so, else false.
	 */
	private static boolean addRepeatChildQtn(QuestionDef qtn, Vector<QuestionDef> repeats,Element child,Hashtable<String, String> map,Hashtable<String, QuestionDef> rptKidmap){
		for(int i=0; i<repeats.size(); i++){
			QuestionDef rptQtn = (QuestionDef)repeats.get(i);
			if(qtn.getVariableName().contains(rptQtn.getVariableName())){
				//rptQtnsDef.addQuestion(qtn); //TODO This is temporarily removed to solve the wiered problem list bug
				String varname = qtn.getVariableName().substring(rptQtn.getVariableName().length()+1);
				map.put(varname, varname);
				rptKidmap.put(varname, qtn);
				return true;		
			}
		}
		return false;
	}

	private static void setQuestionType(QuestionDef def, String type, Element node){
		if(type != null){
			if(type.equals("xsd:string") || type.indexOf("string") != -1 ){
				String format = node.getAttributeValue(null,"format");
				if("gps".equals(format))
					def.setType(QuestionDef.QTN_TYPE_GPS);
				else
					def.setType(QuestionDef.QTN_TYPE_TEXT);
			}
			else if((type.equals("xsd:integer") || type.equals("xsd:int")) || (type.indexOf("integer") != -1 || type.indexOf("int") != -1))
				def.setType(QuestionDef.QTN_TYPE_NUMERIC);
			else if(type.equals("xsd:decimal") || type.indexOf("decimal") != -1 )
				def.setType(QuestionDef.QTN_TYPE_DECIMAL);
			else if(type.equals("xsd:dateTime") || type.indexOf("dateTime") != -1 )
				def.setType(QuestionDef.QTN_TYPE_DATE_TIME);
			else if(type.equals("xsd:time") || type.indexOf("time") != -1 )
				def.setType(QuestionDef.QTN_TYPE_TIME);
			else if(type.equals("xsd:date") || type.indexOf("date") != -1 )
				def.setType(QuestionDef.QTN_TYPE_DATE);
			else if(type.equals("xsd:boolean") || type.indexOf("boolean") != -1 )
				def.setType(QuestionDef.QTN_TYPE_BOOLEAN);
			else if(type.equals("xsd:base64Binary") || type.indexOf("base64Binary") != -1 ){
				String format = node.getAttributeValue(null,"format");
				if("video".equals(format))
					def.setType(QuestionDef.QTN_TYPE_VIDEO);
				else if("audio".equals(format))
					def.setType(QuestionDef.QTN_TYPE_AUDIO);
				else
					def.setType(QuestionDef.QTN_TYPE_IMAGE);
			}
		}
		else
			def.setType(QuestionDef.QTN_TYPE_REPEAT);
	}

	private static byte getAction(Object actn){
		if(actn == null)
			return OpenXdataConstants.ACTION_DISABLE;

		String value = actn.toString();

		String required = null;
		int pos = value.indexOf('|');
		if(pos > 0){
			required = value.substring(pos+1, value.length());
			value = value.substring(0,pos);
		}

		byte action = 0;
		if(value.equalsIgnoreCase(ATTRIBUTE_VALUE_ENABLE))
			action |= OpenXdataConstants.ACTION_ENABLE;
		else if(value.equalsIgnoreCase(ATTRIBUTE_VALUE_DISABLE))
			action |= OpenXdataConstants.ACTION_DISABLE;
		else if(value.equalsIgnoreCase(ATTRIBUTE_VALUE_SHOW))
			action |= OpenXdataConstants.ACTION_SHOW;
		else if(value.equalsIgnoreCase(ATTRIBUTE_VALUE_HIDE))
			action |= OpenXdataConstants.ACTION_HIDE;

		if(required != null && required.equalsIgnoreCase("true()"))
			action |= OpenXdataConstants.ACTION_MAKE_MANDATORY;
		else 
			action |= OpenXdataConstants.ACTION_MAKE_OPTIONAL;

		return action;
	}

	private static void addSkipRules(FormDef formDef, Hashtable<String, String> map, Hashtable<QuestionDef, String> relevants, Hashtable<QuestionDef, String> actions){
		Vector<SkipRule> rules = new Vector<SkipRule>();

		Enumeration<QuestionDef> keys = relevants.keys();
		byte id = 0;
		while(keys.hasMoreElements()){
			QuestionDef qtn = (QuestionDef)keys.nextElement();
			SkipRule skipRule = buildSkipRule(formDef, qtn.getId(),(String)relevants.get(qtn),++id,getAction(actions.get(qtn)));
			if(skipRule != null)
				rules.add(skipRule);
		}

		formDef.setSkipRules(rules);
	}

	private static void addValidationRules(FormDef formDef, Hashtable<String, String> map, Hashtable<QuestionDef, String> constraints, Hashtable<QuestionDef, String> constraintMsgs){
		Vector<ValidationRule> rules = new Vector<ValidationRule>();

		Iterator<QuestionDef> keys = constraints.keySet().iterator();
		while(keys.hasNext()){
			QuestionDef qtn = (QuestionDef)keys.next();
			ValidationRule validationRule = buildValidationRule(formDef, qtn.getId(),(String)constraints.get(qtn),(String)constraintMsgs.get(qtn));
			if(validationRule != null)
				rules.add(validationRule);
		}

		formDef.setValidationRules(rules);
	}

	private static SkipRule buildSkipRule(FormDef formDef, byte questionId, String relevant, byte id, byte action){

		SkipRule skipRule = new SkipRule();
		skipRule.setId(id);
		//TODO For now we are only dealing with hiding and showing.
		skipRule.setAction(action); //TODO This should depend on whats in the xforms file
		skipRule.setConditions(getSkipRuleConditions(formDef,relevant,skipRule.getAction()));
		skipRule.setConditionsOperator(getConditionsOperator(relevant));

		Vector<Byte> actionTargets = new Vector<Byte>();
		actionTargets.add(new Byte(questionId));
		skipRule.setActionTargets(actionTargets);
		//skipRule.setName(name);

		if(skipRule.getConditions() == null || skipRule.getConditions().size() == 0)
			return null;
		return skipRule;
	}

	private static ValidationRule buildValidationRule(FormDef formDef, byte questionId, String constraint, String constraintMsg){

		ValidationRule validationRule = new ValidationRule(questionId,null,null);
		validationRule.setConditions(getValidationRuleConditions(formDef,constraint,questionId));
		validationRule.setConditionsOperator(getConditionsOperator(constraint));

		if(constraintMsg == null)
			validationRule.setErrorMessage("");
		else
			validationRule.setErrorMessage(constraintMsg);

		if(validationRule.getConditions() == null || validationRule.getConditions().size() == 0)
			return null;
		return validationRule;
	}

	private static Vector<Condition> getSkipRuleConditions(FormDef formDef, String relevant, byte action){
		Vector<Condition> conditions = new Vector<Condition>();

		Vector<String> list = getConditionsOperatorTokens(relevant);

		Condition condition  = new Condition();
		for(byte i=0; i<list.size(); i++){
			condition = getSkipRuleCondition(formDef,(String)list.elementAt(i),(byte)(i+1),action);
			if(condition != null)
				conditions.add(condition);
		}

		return conditions;
	}

	private static Vector<Condition> getValidationRuleConditions(FormDef formDef, String constraint, byte questionId){
		Vector<Condition> conditions = new Vector<Condition>();

		Vector<String> list = getConditionsOperatorTokens(constraint);

		Condition condition  = new Condition();
		for(int i=0; i<list.size(); i++){
			condition = getValidationRuleCondition(formDef,(String)list.elementAt(i),questionId);
			if(condition != null)
				conditions.add(condition);
		}

		return conditions;
	}

	private static Condition getSkipRuleCondition(FormDef formDef, String relevant, byte id, byte action){	

		Condition condition  = new Condition();
		condition.setId(id);
		condition.setOperator(getOperator(relevant,action));

		//eg relevant="/data/question10='7'"
		int pos = getOperatorPos(relevant);
		if(pos < 0)
			return null;

		String varName = relevant.substring(0, pos);
		QuestionDef questionDef = formDef.getQuestion(varName.trim());
		if(questionDef == null){
			String prefix = "/" + formDef.getVariableName() + "/";
			if(varName.startsWith(prefix))
				questionDef = formDef.getQuestion(varName.trim().substring(prefix.length(), varName.trim().length()));
			if(questionDef == null)
				return null;
		}
		condition.setQuestionId(questionDef.getId());

		String value;
		//first try a value delimited by '
		int pos2 = relevant.lastIndexOf('\'');
		if(pos2 > 0){
			//pos1++;
			int pos1 = relevant.substring(0, pos2).lastIndexOf('\'',pos2);
			if(pos1 < 0){
				System.out.println("Relevant value not closed with ' characher");
				return null;
			}
			pos1++;
			value = relevant.substring(pos1,pos2);
		}
		else //else we take whole value after operator	
			value = relevant.substring(pos+getOperatorSize(condition.getOperator(),action),relevant.length());

		if(!(value.equals("null") || value.equals(""))){
			condition.setValue(value.trim());

			if(condition.getOperator() == OpenXdataConstants.OPERATOR_NULL)
				return null; //no operator set hence making the condition invalid
		}
		else if(condition.getOperator() == OpenXdataConstants.OPERATOR_NOT_EQUAL)
			condition.setOperator(OpenXdataConstants.OPERATOR_IS_NOT_NULL); //must be != ''
		else
			condition.setOperator(OpenXdataConstants.OPERATOR_IS_NULL); //must be = ''

		return condition;
	}

	private static Condition getValidationRuleCondition(FormDef formDef, String constraint, byte questionId){		
		Condition condition  = new Condition();
		condition.setId(questionId);
		condition.setOperator(getOperator(constraint,OpenXdataConstants.ACTION_ENABLE));
		condition.setQuestionId(questionId);

		//eg . &lt;= 40"
		int pos = getOperatorPos(constraint);
		if(pos < 0)
			return null;

		QuestionDef questionDef = formDef.getQuestion(questionId);
		if(questionDef == null)
			return null;

		String value;
		//first try a value delimited by '
		int pos2 = constraint.lastIndexOf('\'');
		if(pos2 > 0){
			//pos1++;
			int pos1 = constraint.substring(0, pos2).lastIndexOf('\'',pos2);
			if(pos1 < 0){
				System.out.println("constraint value not closed with ' characher");
				return null;
			}
			pos1++;
			value = constraint.substring(pos1,pos2);
		}
		else //else we take whole value after operator	
			value = constraint.substring(pos+getOperatorSize(condition.getOperator(),OpenXdataConstants.ACTION_ENABLE),constraint.length());

		if(!(value.equals("null") || value.equals(""))){
			condition.setValue(value.trim());

			if(condition.getOperator() == OpenXdataConstants.OPERATOR_NULL)
				return null; //no operator set hence making the condition invalid
		}
		else
			condition.setOperator(OpenXdataConstants.OPERATOR_IS_NULL);

		if(constraint.contains("length(.)") || constraint.contains("count(.)"))
			condition.setFunction(OpenXdataConstants.FUNCTION_LENGTH);

		return condition;
	}


	private static boolean isPositiveAction(int action){
		return ((action & OpenXdataConstants.ACTION_ENABLE) != 0) || ((action & OpenXdataConstants.ACTION_SHOW) != 0);
	}

	//TODO Add the other xpath operators
	private static byte getOperator(String relevant, int action){

		if(relevant.indexOf(">=") > 0 || relevant.indexOf("&gt;=") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_GREATER_EQUAL;
			return OpenXdataConstants.OPERATOR_LESS;
		}
		else if(relevant.indexOf('>') > 0 || relevant.indexOf("&gt;") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_GREATER;
			return OpenXdataConstants.OPERATOR_LESS_EQUAL;
		}
		else if(relevant.indexOf("<=") > 0 || relevant.indexOf("&lt;=") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_LESS_EQUAL;
			return OpenXdataConstants.OPERATOR_GREATER;
		}
		else if(relevant.indexOf('<') > 0 || relevant.indexOf("&lt;") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_LESS;
			return OpenXdataConstants.OPERATOR_GREATER_EQUAL;
		}
		else if(relevant.indexOf("!=") > 0 || relevant.indexOf("!=") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_NOT_EQUAL;
			return OpenXdataConstants.OPERATOR_EQUAL;
		}
		else if(relevant.indexOf('=') > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_EQUAL;
			return OpenXdataConstants.OPERATOR_NOT_EQUAL;
		}
		else if(relevant.indexOf("not(starts-with") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_NOT_START_WITH;
			return OpenXdataConstants.OPERATOR_STARTS_WITH;
		}
		else if(relevant.indexOf("starts-with") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_STARTS_WITH;
			return OpenXdataConstants.OPERATOR_NOT_START_WITH;
		}
		else if(relevant.indexOf("not(contains") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_NOT_CONTAIN;
			return OpenXdataConstants.OPERATOR_CONTAINS;
		}
		else if(relevant.indexOf("contains") > 0){
			if(isPositiveAction(action))
				return OpenXdataConstants.OPERATOR_CONTAINS;
			return OpenXdataConstants.OPERATOR_NOT_CONTAIN;
		}

		return OpenXdataConstants.OPERATOR_NULL;
	}

	private static int getOperatorSize(byte operator, int action){
		if(operator == OpenXdataConstants.OPERATOR_GREATER_EQUAL || 
				operator == OpenXdataConstants.OPERATOR_LESS_EQUAL ||
				operator == OpenXdataConstants.OPERATOR_NOT_EQUAL)
			return isPositiveAction(action) ? 2 : 1;
		else if(operator == OpenXdataConstants.OPERATOR_LESS ||
				operator == OpenXdataConstants.OPERATOR_GREATER || 
				operator == OpenXdataConstants.OPERATOR_EQUAL)
			return isPositiveAction(action) ? 1 : 2;

		return 0;
	}

	private static int getOperatorPos(String relevant){
		//Using lastindexof because of expressions like:
		//relevant="/ClinicalData/SubjectData/StudyEventData/FormData/ItemGroupData/ItemData[@ItemOID='I_REVI_IMPROVEMENT']/@Value = '1'"

		int pos = relevant.lastIndexOf("!=");
		if(pos > 0)
			return pos;

		pos = relevant.lastIndexOf(">=");
		if(pos > 0)
			return pos;

		pos = relevant.lastIndexOf("<=");
		if(pos > 0)
			return pos;

		pos = relevant.lastIndexOf('>');
		if(pos > 0)
			return pos;

		pos = relevant.lastIndexOf('<');
		if(pos > 0)
			return pos;

		pos = relevant.lastIndexOf('=');
		if(pos > 0)
			return pos;

		//the order of the code below should not be changed as for example 'starts with' can be taken even when conditon is 'not(starts-with'	
		pos = relevant.lastIndexOf("not(starts-with");
		if(pos > 0)
			return pos;

		pos = relevant.lastIndexOf("starts-with");
		if(pos > 0)
			return pos;

		pos = relevant.lastIndexOf("not(contains");
		if(pos > 0)
			return pos;

		pos = relevant.lastIndexOf("contains");
		if(pos > 0)
			return pos;

		return pos;
	}

	private static Vector<String> getConditionsOperatorTokens(String relevant){
		//TODO For now we are only dealing with one AND or OR, for simplicity
		//If one mixes both in the same relevant statement, then we take the first.
		Vector<String> list = new Vector<String>();

		int pos = 0;
		do{
			pos = extractConditionsOperatorTokens(relevant,pos,list);
		}while(pos > 0);

		return list;
	}

	private static int extractConditionsOperatorTokens(String relevant,int startPos, Vector<String> list){
		int pos,pos2,opSize = 5;

		pos = relevant.toUpperCase().indexOf(" AND ",startPos);
		if(pos <0){
			pos = relevant.toUpperCase().indexOf(" OR ",startPos);
			opSize = 4;
		}

		//AND may be the last token when we have starting ORs hence skipping them. eg (relevant="/data/question10=7 OR /data/question6=4    OR  /data/question8=1 AND /data/question1='daniel'")
		pos2 = relevant.toUpperCase().indexOf(" OR ",startPos);
		if(pos2 > 0 && pos2 < pos){
			pos = pos2;
			opSize = 4;
		}


		if(pos < 0){
			list.add(relevant.substring(startPos).trim());
			opSize = 0;
		}
		else
			list.add(relevant.substring(startPos,pos).trim());

		return pos+opSize;
	}

	private static byte getConditionsOperator(String relevant){
		if(relevant.toUpperCase().indexOf(" AND ") > 0)
			return OpenXdataConstants.CONDITIONS_OPERATOR_AND;
		else if(relevant.toUpperCase().indexOf(" OR ") > 0)
			return OpenXdataConstants.CONDITIONS_OPERATOR_OR;
		return OpenXdataConstants.CONDITIONS_OPERATOR_NULL;
	}

	private static String addNonBindControl(FormDef formDef,Element child,Hashtable<QuestionDef, String> relevants, String ref, String bind){
		QuestionDef qtn = new QuestionDef();
		if(formDef.getPages() == null)
			qtn.setId(getNextQuestionId());
		else{
			if(reachedMaxQuestionId())
				return null;
			qtn.setId(getNextQuestionId());
		}

		if(child.getAttributeValue(null, "type") == null)
			qtn.setType(QuestionDef.QTN_TYPE_TEXT);
		else
			setQuestionType(qtn,child.getAttributeValue(null, "type"),child);

		if(child.getAttributeValue(null, "required") != null && child.getAttributeValue(null, "required").equals("true()")){
			if(child.getAttributeValue(null,"action") == null)
				qtn.setMandatory(true);
		}
		if(child.getAttributeValue(null, "readonly") != null && child.getAttributeValue(null, "readonly").equals("true()"))
			qtn.setEnabled(false);
		if(child.getAttributeValue(null, "locked") != null && child.getAttributeValue(null, "locked").equals("true()"))
			qtn.setLocked(true);
		if(child.getAttributeValue(null, "visible") != null && child.getAttributeValue(null, "visible").equals("false()"))
			qtn.setVisible(false);

		qtn.setVariableName(((ref != null) ? ref : bind));
		formDef.addQuestion(qtn);
		if(child.getAttributeValue(null, "relevant") != null)
			relevants.put(qtn,child.getAttributeValue(null, "relevant"));

		return qtn.getVariableName();
	}
}
