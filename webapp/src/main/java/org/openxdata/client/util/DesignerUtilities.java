package org.openxdata.client.util;

import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.admin.model.FormDefVersion;
import org.purc.purcforms.client.util.FormDesignerUtil;

/**
 * Encapsulates utility functions used by the PurcForms Designer.
 * 
 */
public class DesignerUtilities {

	/**
	 * Creates a unique form binding.
	 * 
	 * @param formDefVersion the form definition-version object we manipulating.
	 * 
	 * @return the form binding.
	 */
	public static String getDefaultFormBinding(FormDefVersion formDefVersion){
		FormDef formDef = formDefVersion.getFormDef();
		String binding = formDef.getStudy().getName() + "_" + formDef.getName() + "_" + formDefVersion.getName();
		return FormDesignerUtil.getXmlTagName(binding);
	}
}
