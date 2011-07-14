package org.openxdata.server.service.impl;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openxdata.server.admin.model.FormDef;
import org.openxdata.server.service.FormService;
import org.openxdata.server.service.UserService;
import org.openxdata.test.BaseContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests methods in the StudyManagerService which deal with FormDef objects
 * 
 * @author daniel
 * 
 */
public class FormDefServiceTest extends BaseContextSensitiveTest {

	@Autowired
	protected UserService userService;
	
	@Autowired
	protected FormService formService;
	
	@Test
	public void getForms_shouldReturnAllForms() throws Exception {
		
		List<FormDef> forms = formService.getForms();
		
		Assert.assertNotNull(forms);
		Assert.assertEquals("The number of forms is 5", 5, forms.size());
		Assert.assertNotNull(getForm("Sample Form",forms));
	}
	
	@Test
	public void saveForm_shouldSaveForm() throws Exception {
		final String formName = "FormName";
		
		List<FormDef> forms = formService.getForms();
		Assert.assertNotNull(forms);
		int numberOfForms = forms.size();
		
		FormDef form = new FormDef();
		form.setName(formName);
		form.setCreator(userService.getUsers().get(0));
		form.setDateCreated(new Date());
		
		formService.saveForm(form);
		
		forms = formService.getForms();
		Assert.assertEquals("Added one form", (numberOfForms+1), forms.size());
		Assert.assertNotNull(getForm(formName,forms));
	}
	
	@Test
	public void deleteForm_shouldDeleteGivenForm() throws Exception {
		
		final String formName = "Form Name";
		
		List<FormDef> forms = formService.getForms();
		int numberOfForms = forms.size();
	
		FormDef form = new FormDef();
		form.setName(formName);
		form.setCreator(userService.getUsers().get(0));
		form.setDateCreated(new Date());
		
		formService.saveForm(form);
		forms = formService.getForms();
		Assert.assertEquals("Added one form, so now there is one more", (numberOfForms+1), forms.size());		
		form = getForm(formName,forms);
		Assert.assertNotNull(form);

		formService.deleteForm(form);
		
		forms = formService.getForms();
		Assert.assertEquals("Deleted the form, so now there is the same", numberOfForms, forms.size());
		Assert.assertNull(getForm(formName,forms));
	}
	
	/**
	 * Gets a form object for a given name from a list of form objects.
	 * 
	 * @param name the name of the form to look for.
	 * @param studies the list of form objects.
	 * @return the form object that matches the given name.
	 */
	private FormDef getForm(String name, List<FormDef> forms){
		for(FormDef form : forms){
			if (form.getName().equals(name)) {
				return form;
			}
		}
		
		return null;
	}
}
