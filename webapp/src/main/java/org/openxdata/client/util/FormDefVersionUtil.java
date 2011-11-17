package org.openxdata.client.util;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.purc.purcforms.client.util.FormDesignerUtil;

public class FormDefVersionUtil {

	/**
	 * generate a unique form binding.
	 */
	public static String generateDefaultFormBinding(FormDefVersion formDefVersion) {
		FormDef formDef = formDefVersion.getFormDef();
		String binding = formDef.getStudy().getName() + "_"
				+ formDef.getName() + "_" + formDefVersion.getName();
		return FormDesignerUtil.getXmlTagName(binding);
	}

	private static String getFormBinding(FormDefVersion formDefVersion) {
		String xform = formDefVersion.getXform();
		String toSearch = "instance id=\"";
		int firstIndex = xform.indexOf(toSearch);
		int secondIndex = xform.indexOf("\"", firstIndex + toSearch.length());
		return xform.substring(firstIndex + toSearch.length(), secondIndex);
	}

	public static void renameFormBinding(FormDefVersion formDefVersion, String id) {
		/*Replaces this form version's binding and all elements that
		 * use the binding name. eg formKey,skiplogic etc
		 */
		formDefVersion.setXform(formDefVersion.getXform().replaceAll(getFormBinding(formDefVersion), id));
	}

	private static String getXformName(FormDefVersion formDefVersion) {
		String toSearch = "name=\"";
		String xform = formDefVersion.getXform();
		int firstIndex = xform.indexOf(toSearch);
		int secondIndex = xform.indexOf("\"", firstIndex + toSearch.length());
		return xform.substring(firstIndex + toSearch.length(), secondIndex);
	}

	public static void renameXformName(FormDefVersion formDefVersion, String newName) {
		String xform = formDefVersion.getXform();
		formDefVersion.setXform(xform.replaceFirst(getXformName(formDefVersion),
				formDefVersion.getFormDef().getName() + "_" + newName));
	}
}
