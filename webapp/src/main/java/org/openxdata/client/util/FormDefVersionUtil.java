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
		String formBinding = null;
		String xform = formDefVersion.getXform();
		String toSearch = "instance id=\"";
		int firstIndex = xform.indexOf(toSearch);
		if (firstIndex != -1) {
			int secondIndex = xform.indexOf("\"", firstIndex + toSearch.length());
			if (secondIndex != -1) {
				formBinding = xform.substring(firstIndex + toSearch.length(), secondIndex);
			}
		}
		return formBinding;
	}

	public static void renameFormBinding(FormDefVersion formDefVersion, String id) {
		/*Replaces this form version's binding and all elements that
		 * use the binding name. eg formKey,skiplogic etc
		 */
		if (formDefVersion.getXform() != null) {
			String formBinding = getFormBinding(formDefVersion);
			if (formBinding != null) {
				formDefVersion.setXform(formDefVersion.getXform().replaceAll(formBinding, id));
			}
		}
	}

	private static String getXformName(FormDefVersion formDefVersion) {
		String xformName = null;
		String toSearch = "name=\"";
		String xform = formDefVersion.getXform();
		int firstIndex = xform.indexOf(toSearch);
		if (firstIndex != -1) {
			int secondIndex = xform.indexOf("\"", firstIndex + toSearch.length());
			if (secondIndex != -1) {
				xformName = xform.substring(firstIndex + toSearch.length(), secondIndex);
			}
		}
		return xformName;
	}

	public static void renameXformName(FormDefVersion formDefVersion, String newName) {
		String xform = formDefVersion.getXform();
		if (xform != null) {
			String xformName = getXformName(formDefVersion);
			if (xformName != null) {
				formDefVersion.setXform(xform.replaceFirst(xformName,
						formDefVersion.getFormDef().getName() + "_" + newName));
			}
		}
	}
}
