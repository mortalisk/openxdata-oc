package org.openxdata.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openxdata.proto.exception.ProtocolAccessDeniedException;
import org.openxdata.proto.exception.ProtocolInvalidSessionReferenceException;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.FormSmsArchive;
import org.openxdata.server.admin.model.FormSmsError;
import org.openxdata.server.admin.model.User;

/**
 * Service that handles download of studies, forms, users, languages, menu text
 * together with upload of collected data for mobile devices.
 * 
 * @author daniel
 *
 */
public interface FormDownloadService {

    /**
     * Gets the list of users. Each item in this list being an array
     * of four objects. The first is an Integer which is the user id, the second
     * is a String which is the user name, the third is a String which is the
     * user hashed password, and the fourth is a String which is the salt used
     * to hash the user password.
     *
     * @return the user list.
     */
    List<Object[]> getUsers();

    /**
     * Gets a list of studies. Each item in this list being an array
     * of two objects. The first is an Integer which is the study id, and the second
     * is a String which is the study name.
     *
     * @param User logged in user
     * @return the study list.
     */
    List<Object[]> getStudyList(User user);

    /**
     * Gets a list of default form versions for a given locale.
     *
     * @param User logged in user
     * @param locale the locale key. eg en
     * @return the list of xml texts for each default form version.
     */
    List<String> getFormsDefaultVersionXml(User user, String locale);

    /**
     * Gets a list of default form versions for a given study and locale.
     *
     * @param User logged in user
     * @param studyId the study identifier.
     * @param locale the locale key.
     * @return the list of xml texts form each default form version.
     */
    List<String> getFormsDefaultVersionXml(User user, Integer studyId, String locale);

    /**
     * Saves form data.
     *
     * @param xml the xforms model xml data to save.
     * @param user the user who is submitting the data.
     * @param creationDate the date when the data is being submitted.
     */
    FormData saveFormData(String xml, User user, Date creationDate);
     
    /**
     * Updates existing form data.
     *
     * @param formDataId Integer previously saved identifier
     * @param xml the xforms model xml data to save.
     * @param user the user who is submitting the data.
     * @param changedDate the date when the data is being modified.
     */
    FormData updateFormData(Integer formDataId, String xml, User user, Date changedDate);
    
    /**
     * Retrieves form data, checking permissions of the user
     * @param user User who is retrieving form data
     * @param formDefVersionId Integer identifier of the form definition version
     * @param formDataId
     * @return FormData
     */
    FormData getFormData(User user, Integer formDefVersionId, Integer formDataId) throws ProtocolInvalidSessionReferenceException, ProtocolAccessDeniedException;
    
    /**
     * Retrieves the form version
     * @param formVersionId
     * @return
     */
    FormDefVersion getFormVersion(Integer formVersionId);

    /**
     * Saves form data.
     *
     * @param formData the form data to save.
     */
    void saveFormData(FormData formData);

    /**
     * Archives successfully processed sms.
     *
     * @param data the sms data archive object.
     */
    void saveFormSmsArchive(FormSmsArchive data);

    /**
     * Saves sms which has resulted into errors during its processing.
     *
     * @param error the sms error object.
     */
    void saveFormSmsError(FormSmsError error);

    /**
     * Writes forms onto a stream.
     *
     * @param os the stream for writing to.
     * @param serializerName the name of the setting which points to the form serializer class.
     * @param locale the locale key.
     */
    void downloadForms(OutputStream os, String serializerName, String locale);

    /**
     * Writes forms, in a given study, onto a stream.
     *
     * @param studyId the identifier of the study whose forms to write.
     * @param os the stream for writing to.
     * @param serializerName the name of the setting which points to the form serializer class.
     * @param locale the locale key.
     */
    void downloadForms(int studyId, OutputStream os, String serializerName, String locale);

    /**
     * Writes all forms, onto a stream.
     *
     * @param os the stream for writing to.
     * @param serializerName the name of the setting which points to the form serializer class.
     * @param locale the locale key.
     */
    void downloadAllForms(OutputStream os, String serializerName, String locale);

    /**
     * Reads submitted form data from a stream.
     *
     * @param is the stream from which to read the submitted data.
     * @param os the stream for writing the response.
     * @param serializerName the name of the setting which points to the form serializer class.
     */
    void submitForms(InputStream is, OutputStream os, String serializerName);

    /**
     * Writes users onto a stream.
     *
     * @param os the stream for writing to.
     * @param serializerName the name of the setting which points to the user serializer class.
     */
    void downloadUsers(OutputStream os, String serializerName);

    /**
     * Writes studies onto a stream.
     *
     * @param os the stream for writing to.
     * @param serializerName the name of the setting which points to the study serializer class.
     * @param locale the locale key.
     */
    void downloadStudies(OutputStream os, String serializerName, String locale);

    /**
     * Writes mobile application menu text onto a stream.
     *
     * @param is the stream from which to read request parameters, if any.
     * @param os the stream for writing to.
     * @param serializerName the name of the setting which points to the serializer class.
     * @param locale the locale key.
     * @deprecated No longer in use, the mobile client have the translation for menu text on the phone
     * @throws IOException
     */
    @Deprecated
    void downloadMenuText(InputStream is, OutputStream os, String serializerName, String locale) throws IOException;

    /**
     * Writes locales or languages onto a stream.
     *
     * @param is the stream from which to read request parameters, if any.
     * @param os the stream for writing to.
     * @param serializerName the name of the setting which points to the form serializer class.
     * @throws IOException
     */
    void downloadLocales(InputStream is, OutputStream os, String serializerName) throws IOException;

    /**
     * Gets a user registered with a given phone number.
     *
     * @param phoneNo the phone number.
     * @return the user.
     */
    User getUserByPhoneNo(String phoneNo);

    Integer getStudyIdWithKey(String studyKey);

    /**
     * Gets a map of all form versions keyed by the form version id.
     *
     * @return the map of xml texts for each form version keyed by the form version id.
     */
    public Map<Integer, String> getFormsVersionXmlMap();
}
