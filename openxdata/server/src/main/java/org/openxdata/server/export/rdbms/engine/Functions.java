/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.export.rdbms.engine;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Class with a list of static methods that can be reused in a number of other classes
 * @author Tumwebaze Charles
 *
 */
public class Functions {

	/**
	 * checks whether a given node is a repeat
	 * @param schemaDocument
	 * schema document from which to check
	 * @param node
	 * node to check
	 * @return
	 * returns a value indicating whether a node is a repeat
	 * true for node being a repeat and false for a node not being a repeat.
	 */
	public static boolean isRepeat(Document schemaDocument, Node node){
		String nodeName = node.getNodeName();
		NodeList repeats = schemaDocument.getElementsByTagNameNS("*", "repeat");
		Node binding = getBinding(schemaDocument, nodeName);
		if(binding != null){
			for(int i = 0; i < repeats.getLength(); i++){
				Node repeat = repeats.item(i);
				NamedNodeMap attributes = repeat.getAttributes();
				if(attributes != null){
					Node att = attributes.getNamedItem("bind");
					if(att != null && att.getNodeValue().equalsIgnoreCase(nodeName)){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * returns the bind with a given id
	 * @param schemaDocument
	 * schema document from which to check
	 * @param value
	 * id of the bind to return
	 * @return
	 * Node
	 */
	private static Node getBinding(Document schemaDocument, String value){
		NodeList bindings = schemaDocument.getElementsByTagNameNS("*", "bind");
		for(int i = 0; i < bindings.getLength(); i++){
			Node bind = bindings.item(i);
			NamedNodeMap attributes = bind.getAttributes();
			if(attributes != null){
				Node att = attributes.getNamedItem("id");
				
				if(att != null && !(att instanceof Text || att instanceof Comment)){
					if(att.getNodeValue().equalsIgnoreCase(value)){
						return bind;
					}
				}
			}
		}

		return null;
	}
	
	/**
	 * cleans the node of the Text data
	 * @param node
	 * node to clean 
	 */
	public static void cleanNode(Node node){
		boolean remove = false;
		String nodename = node.getNodeName();
		
		if(nodename != null){
			//removing is a possibility only if the node is of type #text
			if(nodename.equals("#text"))
				remove = true;
			else
				remove = false;
		}
		
		//Extract the node value
		String valueStr = node.getNodeValue();
		int len = 0;
		
		//Now Trim and check if it needs to be removed
		if(valueStr != null){
			valueStr = valueStr.trim();
			len = valueStr.length();
			if(len > 0){
				//Useful
				remove = false;
				//put the trimmed string back in
				node.setNodeValue(valueStr);
			}
		}
		
		if(remove){
			//to remove, to to the parent node and delete from there
			Node p = node.getParentNode();
			p.removeChild(node);
			
			//recursively clean
			cleanNode(p);
			return;
		}
	}
	
    /**
     * checks whether a node has valid child nodes
     * @param node
     * the node to check
     * @return
     * value indicating whether a node has valid child nodes: true to indicate that a node has 
     * valid child nodes and false to indicate that a node has 
     */
    public static boolean hasValidChildNodes(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                return true;
            }
        }
        return false;
    }
	
	/**
	 * checks whether the node should be processed - makes sure it is not
	 * null, a comment or text node
	 * @param node Node to check
	 * @return true if node should be processed
	 */
	public static boolean isValidNode(Node node) {
	    boolean ret = false;
	    if (node != null && !(node instanceof Text || node instanceof Comment)) {
	        ret = true;
	    }
	    return ret;
	}

	/**
	 * resolves the type of the node 
	 * @param schemaDocument
	 * the schema document from which to resolve the type of the node
	 * @param node
	 * the node whose type is to be resolved
	 * @return
	 * the type of the node
	 */
	public static String resolveType(Document schemaDocument, Node node){
		return resolveType(schemaDocument, node.getNodeName());
	}
	
	public static String resolveType(Document schemaDocument, String columnName){
		String name = columnName;

        //check the xf:bind to see if the type is there
		NodeList bindings = schemaDocument.getElementsByTagNameNS("*", "bind");
		for (int i = 0; i < bindings.getLength(); i++) {
			Node bind = bindings.item(i);
			if (isValidNode(bind)) {
				NamedNodeMap attributes = bind.getAttributes();
				if (attributes != null) {
					Node att = attributes.getNamedItem("id");
					if (isValidNode(att)) {
						if (att.getNodeValue().equalsIgnoreCase(name)) {
							Node type = attributes.getNamedItem("type");
							if (type != null) {
								return getColumnType(type.getNodeValue());
							}
						}
					}
				}
			}
		}
		
		//check the input control to check if the type of the node is there
		NodeList inputs  = schemaDocument.getElementsByTagNameNS("*", "input");
		for (int i = 0; i < inputs.getLength(); i++) {
			Node input  = inputs.item(i);
			if (isValidNode(input)) {
				NamedNodeMap attributes = input.getAttributes();
				if (attributes != null) {
					Node att = attributes.getNamedItem("ref");
					if (isValidNode(att)) {
						if (att.getNodeValue().equalsIgnoreCase(name)) {
							Node type = attributes.getNamedItem("type");
							if (type != null) {
								return getColumnType(type.getNodeValue());
							}
						}
					}
				}
			}
		}
		
		return Constants.TYPE_VARCHAR;
	}
	
	private static String getColumnType(String xformType) {
		if (xformType.trim().equalsIgnoreCase("xsd:string")) {
			return Constants.TYPE_VARCHAR;
		} else if(xformType.trim().equalsIgnoreCase("xsd:int")) {
			return Constants.TYPE_INTEGER;
		} else if(xformType.trim().equalsIgnoreCase("xsd:decimal")) {
			return Constants.TYPE_DECIMAL;
		} else if (xformType.trim().equalsIgnoreCase("xsd:date")) {
			return Constants.TYPE_DATE;
		} else if (xformType.trim().equalsIgnoreCase("xsd:time")) {
			return Constants.TYPE_TIME;
		} else if (xformType.trim().equalsIgnoreCase("xsd:datetime")) {
			return Constants.TYPE_DATETIME;
		} else if(xformType.trim().equalsIgnoreCase("xsd:boolean")) {
			//for the boolean type we make it text for now because 
			//we don't know the exact equivalent of the boolean type in SQL
			return Constants.TYPE_VARCHAR;// Constants.TYPE_BOOLEAN;
		} else if (xformType.trim().equalsIgnoreCase("xsd:base64Binary")) {
			return Constants.TYPE_BINARY;
		}
		return null;
	}
}
