package org.openxdata.server.service.impl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.openxdata.server.OpenXDataConstants;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.admin.model.FormSmsError;
import org.openxdata.server.admin.model.Locale;
import org.openxdata.server.admin.model.User;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.dao.FormDownloadDAO;
import org.openxdata.server.dao.LocaleDAO;
import org.openxdata.server.serializer.StudySerializer;
import org.openxdata.server.serializer.UserSerializer;
import org.openxdata.server.serializer.XformSerializer;
import org.openxdata.server.service.FormDownloadService;
import org.openxdata.server.service.FormService;
import org.openxdata.server.service.SerializationService;
import org.openxdata.server.service.StudyManagerService;
import org.openxdata.server.service.UserService;
import org.openxdata.server.util.LanguageUtil;
import org.openxdata.server.util.XformUtil;
import org.openxdata.server.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Default implementation for form download service.
 * 
 * @author daniel
 *
 */
@Transactional
@Service("formDownloadService")
public class FormDownloadServiceImpl implements FormDownloadService {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private FormDownloadDAO formDownloadDAO;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private StudyManagerService studyManagerService;
	
	@Autowired
	private SerializationService serializationService;

    @Autowired
    private LocaleDAO localeDAO;

	public FormDownloadServiceImpl(){
	}

	public void setFormDownloadDAO(FormDownloadDAO dao) {
		this.formDownloadDAO = dao;
	}

	public void setFormService(FormService formService){
		this.formService = formService;
	}
	
	public void setStudyManagerService(StudyManagerService studyManagerService) {
		this.studyManagerService = studyManagerService;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	@Secured("Perm_View_Users")
	public List<Object[]> getUsers() {
		List<User> users = userService.getUsers();
		List<Object[]> retUsers = new ArrayList<Object[]>();
		for(User user : users){
			Object[] retUser = new Object[4];
			retUser[0] = user.getUserId();
			retUser[1] = user.getName();
			retUser[2] = user.getPassword();
			retUser[3] = user.getSalt();

			retUsers.add(retUser);
		}
		return retUsers;
	}

	@Override
	@Secured("Perm_View_Studies")
	public List<Object[]> getStudyList(User user) {
		List<Object[]> studies = null;
		if(user.hasAdministrativePrivileges()) {
			log.info("User "+ user.getName()+ " is an administrator, so all studies will be loaded");
			studies = formDownloadDAO.getStudyList();
		}
		else {
			studies = formDownloadDAO.getStudyList(user);			
		}
		return studies;
	}

	@Override
	@Secured("Perm_View_Forms")
	public List<String> getFormsDefaultVersionXml(User user, String locale){
		Map<Integer, String> forms = formDownloadDAO.getFormsDefaultVersionXml(user);
		return getFormsDefaultVersionXml(forms,locale);
	}

	/**
	 * Gets a map of default form versions keyed by the form version id.
	 * 
	 * @return the map of xml texts for each default form version keyed by the form version id.
	 */
	@Secured("Perm_View_Forms")
	public Map<Integer,String> getFormsDefaultVersionXmlMap(User user){
		return getFormsVersionXmlMap(formDownloadDAO.getFormsDefaultVersionXml(user));
	}
	
	/**
	 * Gets a map of all form versions keyed by the form version id.
	 * 
	 * @return the map of xml texts for each form version keyed by the form version id.
	 */
	@Override
	@Secured("Perm_View_Forms")
	public Map<Integer,String> getFormsVersionXmlMap(){
		return getFormsVersionXmlMap(formDownloadDAO.getFormsVersionXml());
	}

	@Override
	@Transactional(readOnly=true)
	@Secured("Perm_View_Forms")
	public List<String> getFormsDefaultVersionXml(User user, Integer studyId,String locale){
		Map<Integer, String> formsDefaultVersionXml = formDownloadDAO.getFormsDefaultVersionXml(user, studyId);
		return getFormsDefaultVersionXml(formsDefaultVersionXml,locale);
	}

    @Override
    @Secured("Perm_Add_Form_Data")
    public void saveFormData(FormData formData) {
        try {
        	formService.saveFormData(formData);
        } catch (Throwable ex) {
            log.error("Error while saving form! Form data:" + formData.getData());
            throw new UnexpectedException("Form Data Not Successfully submitted!", ex);
        }
    }

	@Override
	@Secured("Perm_Add_Form_Data")
	public FormData saveFormData(String xml,User user, Date creationDate) {
		return saveFormData(getFormId(xml), xml, null, user, creationDate);
	}

	@Override
	@Secured("Perm_View_Forms")
	public void downloadForms(OutputStream os,String serializerName,String locale) {
		User user = userService.getLoggedInUser();
		XformSerializer formSerializer = serializationService.getFormSerializer(serializerName);
		List<String> formsDefaultVersionXml = getFormsDefaultVersionXml(user, locale);
		formSerializer.serializeForms(os, formsDefaultVersionXml, 1, "Study", "");
	}

	@Override
	@Secured("Perm_View_Forms")
	// note: due to use of service within the method, permission Perm_View_Studies also required
	public void downloadForms(int studyId, OutputStream os,String serializerName,String locale) {
		//We allow users to fill data for only default (Current) form versions
		//and so are the only ones we send back in form download requests.
		User user = userService.getLoggedInUser();
		XformSerializer formSerializer = serializationService.getFormSerializer(serializerName);
		
		String studyKey = studyManagerService.getStudyKey(studyId);
		String studyName = studyManagerService.getStudyName(studyId);
		List<String> formsDefaultVersionXml = getFormsDefaultVersionXml(user, studyId, locale);
		formSerializer.serializeForms(os, formsDefaultVersionXml, studyId, studyName, studyKey);
	}
	
	@Override
	@Secured("Perm_View_Forms")
	public void downloadAllForms(OutputStream os,String serializerName,String locale) {
		User user = userService.getLoggedInUser();
		StudySerializer studySerializer = serializationService.getStudySerializer(serializerName);
		List<Object[]> studies = getStudyList(user);
		List<Object[]> studiesWithForms = new ArrayList<Object[]>();
		for (Object[] study : studies) {
			Integer studyId = (Integer)study[0];
			String studyName = (String)study[1];
			List<String> formsDefaultVersionXml = getFormsDefaultVersionXml(user, studyId, locale);
			Object[] studyWithForms = new Object[3];
			studyWithForms[0] = studyId;
			studyWithForms[1] = studyName;
			studyWithForms[2] = formsDefaultVersionXml;
			studiesWithForms.add(studyWithForms);
		}
		studySerializer.serializeStudies(os, studiesWithForms);
	}

    @Override
    @Secured("Perm_Add_Form_Data")
	public void submitForms(InputStream is, OutputStream os, String serializerName) {
        //When submitting data, we need all the form versions and not just the default ones
        //because the user can change the default form version which already has data on
        //mobile devices. So getting all versions shields us from such problems.
        List<FormData> formDataList = new ArrayList<FormData>();
        User user = userService.getLoggedInUser();
        XformSerializer formSerializer = serializationService.getFormSerializer(serializerName);
        Map<Integer, String> formsVersionXmlMap = getFormsVersionXmlMap();
		List<String> xforms = (List<String>) formSerializer.deSerialize(is, formsVersionXmlMap);
        if (xforms == null || xforms.size() == 0) {
            throw new UnexpectedException("Problem encountered while deserializing data.");
        }
        try {
            for (String xml : xforms) {
                Document doc = XmlUtil.fromString2Doc(xml);
                Integer formId = Integer.valueOf(doc.getDocumentElement().getAttribute(OpenXDataConstants.ATTRIBUTE_NAME_FORMID));
                String descTemplate = doc.getDocumentElement().getAttribute(OpenXDataConstants.ATTRIBUTE_NAME_DESCRIPTION_TEMPLATE);
                FormData formData = saveFormData(formId, xml, XmlUtil.getDescriptionTemplate(doc.getDocumentElement(), descTemplate), user, new Date());
                formDataList.add(formData);
            }
            // serialize the summaries
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeByte(formDataList.size());
            for (FormData fd : formDataList) {
                String description = fd.getDescription();
                if (description != null) {
                    dos.writeUTF(description);
                } else {
                    dos.writeUTF("form {"+fd.getFormDefVersionId()+"}"); // description can be null
                }
                dos.writeInt(fd.getFormDataId());
            }
        } catch (IOException ex) {
        	throw new UnexpectedException(ex);
        }
    }

	@Override
	@Secured("Perm_View_Users")
	public void downloadUsers(OutputStream os,String serializerName) {
		UserSerializer userSerializer = serializationService.getUserSerializer(serializerName);
		userSerializer.serializeUsers(os, getUsers());
	}

	@Override
	@Secured("Perm_View_Studies")
	public void downloadStudies(OutputStream os,String serializerName,String locale) {
		User user = userService.getLoggedInUser();
		StudySerializer studySerializer = serializationService.getStudySerializer(serializerName);
		studySerializer.serializeStudies(os, getStudyList(user));
	}
	
    /**
     * Gets the form id contained in an xml document.
     *
     * @param xml the xml for the document.
     * @return the formId.
     */
    private Integer getFormId(String xml) {
        Document doc = XmlUtil.fromString2Doc(xml);
        return getXformFormId(doc.getDocumentElement());
    }
	
	/**
	 * Gets the form id contained in an xforms document.
	 * 
	 * @param element the root element of the xforms document.
	 * @return the formId.
	 */
	private Integer getXformFormId(Element element){
		String value = element.getAttribute(OpenXDataConstants.ATTRIBUTE_NAME_FORMID);

		if(value == null || value.trim().length() == 0)
			value = getDataNodeAttributeValue(element, OpenXDataConstants.ATTRIBUTE_NAME_FORMID);

		if(validId(value))
			return Integer.parseInt(value);

		return null;
	}
	
	/**
	 * Checks if text contains a valid numeric id.
	 * 
	 * @param id the text to check.
	 * @return true if it has, else false.
	 */
	private boolean validId(String id){
		try{
			Integer.parseInt(id);
			return true;
		}
		catch(Exception ex){
			return false;
		}
	}
	
	/**
	 * Gets a data node attribute value of an xforms document.
	 * 
	 * @param element the root of the xforms document.
	 * @param name the name of the attribute.
	 * @return the value of the data node attribute.
	 */
	private String getDataNodeAttributeValue(Element element, String name){
		Element instanceNode = getInstanceNode(element);
		if(instanceNode == null)
			return null;

		NodeList kids = instanceNode.getChildNodes();
		for(int index = 0; index < kids.getLength(); index++){
			Node child = kids.item(index);
			if(child.getNodeType() != Node.ELEMENT_NODE)
				continue;

			return ((Element)child).getAttribute(name);
		}
		return null;
	}
	
	/**
	 * Gets the instance node of an xforms document.
	 * 
	 * @param parent the root node of the xforms document.
	 * @return the instance node.
	 */
	private Element getInstanceNode(Element parent){
		NodeList kids = parent.getChildNodes();
		for(int index = 0; index < kids.getLength(); index++){
			Node child = kids.item(index);
			if(child.getNodeType() != Node.ELEMENT_NODE)
				continue;

			if(child.getNodeName().contains("instance"))
				return (Element)child;
			else{
				child = getInstanceNode((Element)child);
				if(child != null)
					return (Element)child;
			}
		}

		return null;
	}
	
	/**
	 * Gets a list of default xforms in a given locale.
	 * 
	 * @param forms a map of the xforms keyed by their ids.
	 * @param locale the locale key.
	 * @return the xforms list.
	 */
    private List<String> getFormsDefaultVersionXml(Map<Integer, String> forms, String locale) {
        Assert.notNull(forms, "Forms cant be null!");

        List<String> xforms = new ArrayList<String>();
        Iterator<Entry<Integer, String>> iterator = forms.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, String> entry = iterator.next();

            String xml = LanguageUtil.translate(entry.getValue(), formDownloadDAO.getXformLocaleText(entry.getKey(), locale));
            if (xml != null) {
                xforms.add(xml);
            }
        }
        return xforms;
    }
	
	/**
	 * Takes a map form versions keyed by their ids and puts the ids in the xforms xml.
	 * The form id is put as a of the id attribute in the xforms model child node.
	 * 
	 * @param forms a map form versions keyed by their ids.
	 * @return the map form versions keyed by their ids. 
	 */
	private Map<Integer,String> getFormsVersionXmlMap(Map<Integer,String> forms/*, String locale*/){
		try{
			if(forms != null){
				Iterator<Entry<Integer,String>> iterator = forms.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<Integer,String> entry = iterator.next();				
					entry.setValue(XformUtil.addFormId2Xform(entry.getKey(),entry.getValue()));
				}
				return forms;
			}
		}
		catch(Exception ex){
			log.error(ex.getMessage(), ex);
			ex.printStackTrace();
		}
		return null;
	}

	@Override
	@Secured("Perm_View_Locales")
	public void downloadLocales(InputStream is,OutputStream os,String serializerName) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);

		List<Locale> locales = localeDAO.getLocales();
		if(locales == null || locales.size() == 0)
			dos.writeByte(0);
		else{
			dos.writeByte(locales.size());
			for(Locale locale : locales){
				dos.writeUTF(locale.getKey());
				dos.writeUTF(locale.getName());
			}
		}
	}
	
	@Override
    @Deprecated
	public void downloadMenuText(InputStream is,OutputStream os,String serializerName,String locale) {
		DataOutputStream dos = new DataOutputStream(os);
		
		try {
			dos.writeShort(0);
		}
		catch(Exception ex) {
			throw new UnexpectedException(ex);
		}
	}

	@Override
	// FIXME: no security for SMS
	public void saveFormSmsArchive(FormSmsArchive data)  {
		formDownloadDAO.saveFormSmsArchive(data);
	}
	
	@Override
	// FIXME: no security for SMS
	public void saveFormSmsError(FormSmsError error)  {
		formDownloadDAO.saveFormSmsError(error);
	}
	
	@Override
	@Secured("Perm_View_Users")
	public User getUserByPhoneNo(String phoneNo){
		return formDownloadDAO.getUserByPhoneNo(phoneNo);
	}
	
	/**
	 * Saves submitted form data.
	 * 
	 * @param versionId the identifier of the form version to which the data belongs.
	 * @param xml the xforms model containing the data.
	 * @param description the description of the data.
	 * @param user the user who submitted the data.
	 * @param creationDate the date when data was submitted.
	 * @throws Exception
	 */
	private FormData saveFormData(int versionId, String xml, String description, User user, Date creationDate) {
		FormData formData = new FormData(versionId,xml,description,creationDate,user);
		saveFormData(formData);
		return formData;
	}
	
	@Override
	@Secured("Perm_View_Studies")
	public Integer getStudyIdWithKey(String studyKey){
		return formDownloadDAO.getStudyIdWithKey(studyKey);
	}
}
