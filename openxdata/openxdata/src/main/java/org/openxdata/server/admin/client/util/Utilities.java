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
package org.openxdata.server.admin.client.util;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.openxdata.server.admin.client.Context;
import org.openxdata.server.admin.client.view.widget.OpenXDataMessageDialog;
import org.openxdata.server.admin.client.view.widget.OpenXDataLabel;
import org.openxdata.server.admin.client.view.factory.OpenXDataWidgetFactory;
import org.openxdata.server.admin.model.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import java.util.ArrayList;
import org.openxdata.server.admin.client.view.widget.OpenXDataFlexTable;
import org.openxdata.server.admin.model.Editable;
import org.openxdata.server.admin.model.Report;
import org.openxdata.server.admin.model.ReportGroup;
import org.purc.purcforms.client.util.FormUtil;
import org.purc.purcforms.client.xforms.XformUtil;
import org.purc.purcforms.client.xforms.XmlUtil;
import org.purc.purcforms.client.xpath.XPathExpression;

/**
 * Utilities used by the the server admin.
 * 
 * @author daniel
 * @author Angel
 * 
 */
public class Utilities {
	
	/** Handle to <tt>Widget Factory.</tt> */
	public static OpenXDataWidgetFactory widgetFactory;
	
	/**
	 * Maximizes a widget.
	 * 
	 * @param widget
	 *            the widget to maximise.
	 */
	public static void maximizeWidget(Widget widget) {
		widget.setSize("100%", "100%");
	}
	
	/**
	 * Creates an HTML fragment that places an image & caption together, for use
	 * in a group header.
	 * 
	 * @param imageProto
	 *            an image prototype for an image
	 * @param caption
	 *            the group caption
	 * @return the header HTML fragment
	 */
	public static String createHeaderHTML(ImageResource imageProto,
	        String caption) {
		
		// Add the image and text to a horizontal panel
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(0);
		
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.add(FormUtil.createImage(imageProto));
		Widget headerText = new Label(caption);
		hPanel.add(headerText);
		
		return hPanel.getElement().getString();
	}
	
	/**
	 * Makes a text box widget allow only numeric input.
	 * 
	 * @param textBox
	 *            the text box widget.
	 * @param allowDecimal
	 *            set to true to allow decimal points.
	 */
	public static void allowNumericOnly(TextBox textBox, boolean allowDecimal) {
		final boolean allowDecimalPoints = allowDecimal;
		textBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char keyCode = event.getCharCode();
				Object sender = event.getSource();
				if ((!Character.isDigit(keyCode))
				        && (keyCode != (char) KeyCodes.KEY_TAB)
				        && (keyCode != (char) KeyCodes.KEY_BACKSPACE)
				        && (keyCode != (char) KeyCodes.KEY_LEFT)
				        && (keyCode != (char) KeyCodes.KEY_UP)
				        && (keyCode != (char) KeyCodes.KEY_RIGHT)
				        && (keyCode != (char) KeyCodes.KEY_DOWN)) {
					
					if (keyCode == '.' && allowDecimalPoints
					        && !((TextBox) sender).getText().contains("."))
						return;
					((TextBox) sender).cancelKey();
				}
			}
		});
	}
	
	/**
	 * Gets the index of a tree item which is at the root level.
	 * 
	 * @param tree
	 *            the tree widget.
	 * @param item
	 *            the tree root item whose index we are to get.
	 * @return the index of the tree item.
	 */
	public static int getRootItemIndex(Tree tree, TreeItem item) {
		int count = tree.getItemCount();
		for (int index = 0; index < count; index++) {
			if (item == tree.getItem(index))
				return index;
		}
		return 0;
	}
	
	/**
	 * Removes a root tree item and selects the next one.
	 * 
	 * @param tree
	 *            the tree widget.
	 * @param item
	 *            the root tree item to remove.
	 */
	public static void removeRootItem(Tree tree, TreeItem item) {
		int index = getRootItemIndex(tree, item);
		item.remove();
		
		int count = tree.getItemCount();
		
		// If we have any items left, select the one which was after
		// the one we have just removed.
		if (count > 0) {
			
			// If we have deleted the last item, select the item which was
			// before it.
			if (index == count)
				index--;
			tree.setSelectedItem(tree.getItem(index));
		}
	}
	
	/**
	 * Gets the value of the description template from an xforms instance data
	 * node.
	 * 
	 * @param node
	 *            the instance data node.
	 * @param template
	 *            the description template.
	 * @return the description template value.
	 */
	private static String getDescriptionTemplate(Element dataNode,
	        String template) {
		if (template == null || template.trim().length() == 0)
			return null;
		
		// String s = "Where does ${name}$ come from?";
		String f, v, text = template;
		
		int startIndex, j, i = 0;
		do {
			startIndex = i; // mark the point where we found the first $
			                // character.
			
			i = text.indexOf("${", startIndex); // check the opening $ character
			if (i == -1)
				break; // token not found.
				
			j = text.indexOf("}$", i + 1); // check the closing $ character
			if (j == -1)
				break; // closing token not found. possibly wrong syntax.
				
			f = text.substring(0, i); // get the text before token
			v = getValue(dataNode, text.substring(i + 2, j)); // append value of
			                                                  // token.
			
			f += (v == null) ? "" : v;
			f += text.substring(j + 2, text.length()); // append value after
			                                           // token.
			
			text = f;
			
		} while (true); // will break out when dollar symbols are out.
		
		return text;
	}
	
	/**
	 * Gets the text or attribute value of an xpath expression from an xforms
	 * instance data node.
	 * 
	 * @param dataNode
	 *            the instance data node.
	 * @param xpath
	 *            the xpath expression.
	 * @return the text or attribute value.
	 */
	private static String getValue(Element dataNode, String xpath) {
		int pos = xpath.lastIndexOf('@');
		String attributeName = null;
		if (pos > 0) {
			attributeName = xpath.substring(pos + 1, xpath.length());
			xpath = xpath.substring(0, pos - 1);
		}
		
		XPathExpression xpls = new XPathExpression(dataNode, xpath);
		Vector<?> result = xpls.getResult();
		
		for (Enumeration<?> e = result.elements(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof Element) {
				if (pos > 0) // Check if we are to set attribute value.
					return ((Element) obj).getAttribute(attributeName);
				else
					return XmlUtil.getTextValue(((Element) obj));
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the description template from an xforms document.
	 * 
	 * @param xformXml
	 *            , the xforms xml. This should not be the data xml since it may
	 *            have been created and saved without the description template.
	 * 
	 * @return the description template value.
	 */
	public static String getDescriptionTemplate(String xformXml, String dataXml) {
		Element node = XformUtil.getInstanceDataNode(XmlUtil
		        .getDocument(xformXml));
		if (node == null)
			return null;
		String descTemplate = node.getAttribute("description-template");
		return getDescriptionTemplate(XmlUtil.getDocument(dataXml)
		        .getDocumentElement(), descTemplate);
	}
	
	/**
	 * Disables the browsers default context menu for the specified element.
	 * 
	 * @param elem
	 *            the element whose context menu will be disabled
	 */
	public static native void disableContextMenu(
	        com.google.gwt.user.client.Element elem) /*-{
	                                                 elem.oncontextmenu=function() {  return false};
	                                                 }-*/;
	
	/**
	 * Models the <code>User</code> Status types as <code>Strings</code>
	 * 
	 * @return Status type <code>instance of String</code>.
	 */
	public static List<String> getUserStatusTypes() {
		
		// Should probably be gotten from the db.
		Vector<String> types = new Vector<String>();
		
		types.add("Active");
		types.add("Disabled");
		types.add("Pending Approval");
		
		return types;
	}
	
	/**
	 * Checks to see if the <code>User</code> Password is of the specified
	 * length.
	 * 
	 * @param user
	 *            - <code>User object</code> whose password is being checked
	 * @return - <code>True If(User.Password.Length() > 6)</code>
	 *         <p>
	 *         <code>else false
	 *         </p>
	 *         </code>
	 */
	public static boolean validateUserPassword(User user) {
		boolean validated = false;
		
		if (user != null) {
			String systemSettingPasswordLengthValue = Context.getSetting(
			        "defaultPasswordLength", "6");
			int passwordLengthValue = Integer
			        .parseInt(systemSettingPasswordLengthValue);
			
			if (!user.isNew())
				return true;
			
			if (user.isNew() && user.getClearTextPassword() != null) {
				
				if (user.getClearTextPassword().length() >= passwordLengthValue)
					validated = true;
				else {
					
					String message = "The User password specified is less than the default length that is specified in the system. "
					        + "The Password should be equal or more than "
					        + Context.getSetting("defaultUserPasswordLength",
					                "6") + " characters.";
					
					displayMessage(message);
					
					validated = false;
				}
				
			}
		}
		
		return validated;
	}
	
	/**
	 * Displays a notification message to the <code>User</code>
	 * 
	 * @param message
	 *            Message to display.
	 */
	public static void displayMessage(String message) {
		OpenXDataMessageDialog messageDialog = new OpenXDataMessageDialog(
		        "System Notification");
		
		messageDialog.setMessageToDisplay(message);
		messageDialog.show();
	}
	
	/**
	 * Selects the first <tt>item</tt> on a given <tt>Tree View.</tt>
	 * 
	 * @param tree
	 *            <tt>Tree View</tt> to select <tt>item</tt> on.
	 */
	public static void selectFirstItemOnTreeView(Tree tree) {
		if (tree != null) {
			// Select and expand the first study.
			if (tree.getItemCount() > 0) {
				TreeItem item = tree.getItem(0);
				tree.setSelectedItem(item);
				item.setState(true);
				item.setSelected(true);
			}
		}
	}
	
	/**
	 * Sets the given Message onto the notification bar to show progress.
	 * 
	 * @param message
	 *            Message to show.
	 */
	public static void displayNotificationMessage(String message) {
		widgetFactory.getNotificationLabel().setText(message);
	}
	
	/**
	 * Displays notification Messages one after another with a time lag of 5
	 * seconds.
	 * 
	 * @param firstMessage
	 *            First Message to display.
	 * @param secondMessage
	 *            Second Message to display.
	 * @param lastMessage
	 *            Last Message flag to indicate completion of operation.
	 */
	public static void displayNotificationMessageAsynchronously(
	        String firstMessage, final String secondMessage, String lastMessage) {
		
		// We run in a timer to be able
		Timer timer = new Timer() {
			@Override
			public void run() {
				widgetFactory.getNotificationLabel().setText(secondMessage);
			}
		};
		
		// Wait for 1 second before displaying another message
		timer.schedule(1000);
		
		// Make the timer repeat to display the second message
		timer.scheduleRepeating(5000);
		widgetFactory.getNotificationLabel().setText(firstMessage);
		
		// Stops the timer
		timer.cancel();
		((OpenXDataLabel) widgetFactory.getNotificationLabel())
		        .setDefaultText();
		// Display the second Message
	}
	
	/**
	 * Displays an <tt>Error Message</tt> on the <tt>Notification Bar</tt> with
	 * appropriate formatting.
	 * 
	 * @param failureMessage
	 *            <tt>Message</tt> to display.
	 */
	public static void displayFailureNotificationMessage(String failureMessage) {
		// Display the failure Message
		((OpenXDataLabel) widgetFactory.getNotificationLabel())
		        .setFailureText(failureMessage);
		
	}
	
	/**
	 * Sets the <tt>Widget Factory.</tt>
	 * 
	 * @param widgetFactory
	 *            <tt>Widget Factory to set.</tt>
	 */
	public static void setWidgetFactory(
	        OpenXDataWidgetFactory openxdataWidgetFactory) {
		Utilities.widgetFactory = openxdataWidgetFactory;
	}
	
	/**
	 * Validates a given Email address using a regular expression.
	 * <p>
	 * The simplest validation (99% of email address). For example does not
	 * allow for IP address emails which are rarely used.
	 * </p>
	 * 
	 * @param emailAddress
	 *            Email/Emails to validate. Assumes they are separated with ','.
	 * @return <code>True only and only if given text matches the regular expression.</code>
	 */
	public static boolean validateEmail(String emailAddress) {
		
		boolean validated = false;
		if (emailAddress.contains(",")) {
			String[] emails = emailAddress.split(",");
			for (int index = 0; index < emails.length;) {
				if (emails[index] != null && !emails[index].equals("")) {
					validated = validateEmail(emails[index]);
				}
				
				index++;
				
				if (validated == false)
					break;
				
			}
		} else {
			if (emailAddress != null && !emailAddress.equals("")) {
				validated = emailAddress.trim().matches(
				        "^[\\w-]+(\\.[\\w-]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7}$");
			}
		}
		return validated;
	}
	
	/**
	 * Initializes the <code>Tree View</code> with <code>User Objects.</code>
	 */
	public static void initializeTreeView(Tree tree) {
		
		// Select and expand the first study.
		if (tree.getItemCount() > 0) {
			TreeItem item = tree.getItem(0);
			tree.setSelectedItem(item);
			item.setState(true);
		}
	}
	
	/**
	 * Checks if a given <tt>String</tt> is <tt>Null</tt> or empty.
	 * 
	 * @param string
	 *            <tt>String</tt> to check.
	 * @return <tt>string == null || string.trim().length() == 0</tt>
	 */
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}
	
	/**
	 * Checks if the container is local host.
	 * 
	 * @return <tt>Only and only if the URL contains localhost(Tomcat) or 127.0.0.1(Jetty).</tt>
	 */
	public static boolean isContainerLocalHost() {
		String url = GWT.getHostPageBaseURL();
		if (url.startsWith("http://localhost")
		        || url.startsWith("http://127.0.0.1"))
			return true;
		
		return false;
	}
	
	/**
	 * Checks if the User name is guyzb.
	 * 
	 * @param username
	 *            <tt>User name</tt> we checking.
	 * 
	 * @return <tt>username.equalsIgnoreCase("guyzb")</tt>
	 */
	public static boolean isUserGuyzb(String username) {
		return username.equalsIgnoreCase("guyzb");
	}

    public static <E extends Editable> List<E> getDirtyEditables(List<E> editables) {
        List<E> dirtyEditables = new ArrayList<E>();
        for (E editable : editables) {
            if (editable.isDirty() || editable.isNew())
                dirtyEditables.add(editable);
        }
        return dirtyEditables;
    }

    public static <E extends Editable> List<E> getNewEditiables(List<E> editables) {
        List<E> newItems = new ArrayList<E>();
        for (E editable : editables) {
            if (editable.isNew())
                newItems.add(editable);
        }
        return newItems;
    }

    public static boolean hasNewItems(List<? extends Editable> editables) {
        for (Editable editable : editables) {
            if (editable.isNew())
                return true;
        }
        return false;
    }

    public static void showProgress(String prgMsg) {
        FormUtil.dlg.setText(prgMsg);
        FormUtil.dlg.center();
    }

    public static Widget getNoAccessWidget(String message) {
        FlexTable mtable = new OpenXDataFlexTable();
        mtable.setWidget(0, 0,
                new Label(message));
        FlexCellFormatter mcellFormatter = mtable.getFlexCellFormatter();
        mcellFormatter.setWidth(0, 0, "20%");
        mtable.setStyleName("cw-FlexTable");
        Utilities.maximizeWidget(mtable);
        return mtable;
    }

    public static List<Report> extractReports(List<ReportGroup> groups) {
        List<Report> reports = new ArrayList<Report>();
        if (groups != null) {
            for (ReportGroup reportGroup : groups) {
                if (reportGroup.getGroups() != null) {
                    reports.addAll(extractReports(reportGroup.getGroups()));
                }
                if (reportGroup.getReports() != null)
                    reports.addAll(reportGroup.getReports());
            }
        }
        return reports;
    }
}
