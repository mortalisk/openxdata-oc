/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openxdata.server.serializer.Util;

import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;

/**
 *
 * Transforms an xform to an xhtml document.
 *
 * @author daniel
 * @modified hbuletwenda
 */
public class XformUtil {

    /**
	 * Converts an xform to an xhtml document.
	 *
	 * @param xform the xform
	 * @param xsl the xslt
	 * @return the xhtml representation of the xform.
	 */
	public static String fromXform2Xhtml(String xform, String xsl) throws Exception{

		if(xsl == null) xsl = getXSLT1();
		StringWriter outWriter = new StringWriter();
		Source source = new StreamSource(IOUtils.toInputStream(xform));
		Source xslt = new StreamSource(IOUtils.toInputStream(xsl));
		Result result = new StreamResult(outWriter);

		//System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory tf = TransformerFactory.newInstance();

		Transformer t = tf.newTransformer(xslt);
		t.transform(source, result);
		return outWriter.toString();

	}

	/**
	 * Gets the default CSS for XForms.
	 *
	 * @return the CSS text
	 */
	private static String getDefaultStyle(){

		return "@namespace xf url(http://www.w3.org/2002/xforms); "+
			"/* Display a red background on all invalid form controls */ "+
			"*:invalid .xf-value { background-color: red; } "+
			" "+
			"/* Display a red asterisk after all required form controls */ "+
			"*:required::after { content: '*'; color: red; } "+
			" "+
			"/* Do not render non-relevant form controls */ "+
			"*:disabled { visibility: hidden; } "+
			" "+
			"/* Display an alert message when appropriate */ "+
			"*:valid   xf|alert { display: none; } "+
			"*:invalid xf|alert { display: show; } "+
			" "+
			"/* Display the selected repeat-item with a light blue color. */ "+
			".xf-repeat-index { background-color: lightblue; } "+
			" "+
			"/* Display repeat items in a table row. */ "+
			"xf|repeat .xf-repeat-item {display: table-row;} "+
			" "+
			"/* Display each select1 and input control within a repeat as a table cell, having a thin solid border and its lable aligned centrally. */ "+
			"xf|repeat xf|select1, xf|repeat xf|input{display: table-cell; border: thin; border-style: solid; text-align: center;}" +
			" " +
			"xf|input xf|label,xf|select xf|label,xf|select1 xf|label {width: 32ex; text-align: right; vertical-align: top; padding-right: 0.5em; padding-top: 1ex; padding-bottom: 1ex;} " +
		    " "+
			"xf|item xf|label {width: 100%; text-align: left; padding-right: 0em; padding-bottom: 0ex; padding-top: 0ex;} " +
		    " "+
			"xf|select {padding-top: 1ex; padding-bottom: 1ex;} " +
		    " "+
		    "xf|input, xf|select1, xf|select, xf|submit, xf|item { display: table-row; } "+
		    " "+
		    "xf|input xf|label, xf|select1 xf|label, xf|select xf|label { display: table-cell; } ";

	}

	//<xsl:number value="position()" format="1" />
	/**
	 * Gets the default XSLT for transforming an XForm into an XHTML document.
	 *
	 * @return the XSLT text
	 */
	private static String getDefaultXSLT(){

		return "<?xml version='1.0' encoding='UTF-8'?> "+
			"<xsl:stylesheet version='2.0' "+
			"xmlns:xsl='http://www.w3.org/1999/XSL/Transform' "+
			"xmlns:fn='http://www.w3.org/2005/xpath-functions' "+
			"xmlns:xf='http://www.w3.org/2002/xforms'> "+
			"<xsl:output method='xml' version='1.0' encoding='UTF-8'/> "+
			"<xsl:template match='/'> "+
			" <html xmlns='http://www.w3.org/1999/xhtml' "+
			"       xmlns:xf='http://www.w3.org/2002/xforms' "+
			"       xmlns:xsd='http://www.w3.org/2001/XMLSchema' "+
			"       xmlns:xs='http://www.w3.org/2001/XMLSchema' "+
			"       xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "+
			"       xmlns:ev='http://www.w3.org/2001/xml-events' "+
			" > " +
			" <head> "+
			" 	<title> " +
			"		<xsl:value-of select='/xf:xforms/xf:model/xf:instance/*/@name'/> " +
			"	</title>"+
			" 	<xsl:copy-of select='/xf:xforms/xf:model' /> "+
			" </head> "+
			" <body> "+
			" 	<xsl:for-each select='/xf:xforms/*'> "+
			"   	<xsl:if test='local-name() != \"xf:model\"'> "+
			" 			<xsl:copy-of select='.' /> "+
			"       </xsl:if> "+
			" 	</xsl:for-each> "+
			" </body> "+
			" </html> "+
			"</xsl:template> "+
			"</xsl:stylesheet> ";

		//"	<style> "+ getDefaultStyle() + " </style> "+
		//"   <script type='text/javascript'> <![CDATA[ "+ getJavaStriptNode() + " ]]> </script> "+

	}

        public static String getXSLT1(){

		return "<?xml version='1.0' encoding='UTF-8'?> " +
                        "<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'> " +
                        "   <xsl:output method='xml' version='1.0' encoding='UTF-8'/> " +
                        "   <xsl:template match='/'> " +
                        "       <h:html xmlns='http://www.w3.org/2002/xforms' " +
                        "       xmlns:h='http://www.w3.org/1999/xhtml'" +
                        "       xmlns:ev='http://www.w3.org/2001/xml-events' " +
                        "       xmlns:xsd='http://www.w3.org/2001/XMLSchema' " +
                        "       xmlns:jr='http://openrosa.org/javarosa'> " +
                        "           <h:head> " +
                        "               <h:title> " +
                        "                   <xsl:value-of select='/xf:xforms/xf:model/xf:instance[position()=1]/*[position()=1]/@name'/> " +
                        "               </h:title> " +
                        "               <xsl:copy-of select='/xf:xforms/xf:model' /> " +
                        "           </h:head> " +
                        "           <h:body> " +
                        "               <xsl:for-each select='/xf:xforms/*'> " +
                        "                   <xsl:if test=\"local-name() != 'model'\"> " +
                        "                       <xsl:copy-of select='.' /> " +
                        "                   </xsl:if> " +
                        "               </xsl:for-each> " +
                        "           </h:body> " +
                        "       </h:html> " +
                        "   </xsl:template> " +
                        "</xsl:stylesheet> ";
		
		//"	<style> "+ getDefaultStyle() + " </style> "+
		//"   <script type='text/javascript'> <![CDATA[ "+ getJavaStriptNode() + " ]]> </script> "+

	}

	/**
	 * Gets the javascript needed during the xforms processsing in the browser.
	 * For now the javascript we have deals with deleting of xform repeat items.
	 *
	 * @return the javascript script.
	 */
	private static String getJavaStriptNode(){

		String script = "function deleteRepeatItem(id){ " +
                   "        var model = document.getElementById('modelId'); " +
                   "        var instance = model.getInstanceDocument('instanceId'); " +
                   "        var dataElement = instance.getElementsByTagName('problem_list')[0]; " +
                   "        var itemElements = dataElement.getElementsByTagName(id); " +
                   "        var cnt = itemElements.length; " +

                   "        if (cnt > 1){ " +
                   "             dataElement.removeChild(itemElements[cnt-1]); " +
                   "        } else { " +
				   " 			var values = itemElements[0].getElementsByTagName('value'); " +
				   " 			for(var i=0; i<values.length; i++) " +
				   "			values[i].childNodes[0].nodeValue = null; " +
                   "        } " +

                   "        model.rebuild(); " +
                   "        model.recalculate(); " +
                   "        model.refresh(); " +
                   "   } ";

		return script;

	}

}
