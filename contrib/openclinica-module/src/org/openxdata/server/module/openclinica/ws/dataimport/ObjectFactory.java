
package org.openxdata.server.module.openclinica.ws.dataimport;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.openxdata.server.module.openclinica.ws.dataimport.AuditMessagesType;
import org.openxdata.server.module.openclinica.ws.dataimport.CrfListType;
import org.openxdata.server.module.openclinica.ws.dataimport.CrfVersionType;
import org.openxdata.server.module.openclinica.ws.dataimport.EventListType;
import org.openxdata.server.module.openclinica.ws.dataimport.ImportDataResponse;
import org.openxdata.server.module.openclinica.ws.dataimport.ObjectFactory;
import org.openxdata.server.module.openclinica.ws.dataimport.StudyEventType;
import org.openxdata.server.module.openclinica.ws.dataimport.StudyIdType;
import org.openxdata.server.module.openclinica.ws.dataimport.StudyListType;
import org.openxdata.server.module.openclinica.ws.dataimport.StudyType;
import org.openxdata.server.module.openclinica.ws.dataimport.SubjectEventType;
import org.openxdata.server.module.openclinica.ws.dataimport.SubjectGetType;
import org.openxdata.server.module.openclinica.ws.dataimport.SubjectListGetType;
import org.openxdata.server.module.openclinica.ws.dataimport.SubjectListType;
import org.openxdata.server.module.openclinica.ws.dataimport.UserType;
import org.openxdata.server.module.openclinica.ws.dataimport.UsersType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openxdata.server.module.openclinica.ws.dataimport package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ImportDataRequest_QNAME = new QName("http://openclinica.org/ws/dataImport/v1", "importDataRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openxdata.server.module.openclinica.ws.dataimport
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UsersType }
     * 
     */
    public UsersType createUsersType() {
        return new UsersType();
    }

    /**
     * Create an instance of {@link ImportDataResponse }
     * 
     */
    public ImportDataResponse createImportDataResponse() {
        return new ImportDataResponse();
    }

    /**
     * Create an instance of {@link UserType }
     * 
     */
    public UserType createUserType() {
        return new UserType();
    }

    /**
     * Create an instance of {@link SubjectListType }
     * 
     */
    public SubjectListType createSubjectListType() {
        return new SubjectListType();
    }

    /**
     * Create an instance of {@link StudyListType }
     * 
     */
    public StudyListType createStudyListType() {
        return new StudyListType();
    }

    /**
     * Create an instance of {@link SubjectEventType }
     * 
     */
    public SubjectEventType createSubjectEventType() {
        return new SubjectEventType();
    }

    /**
     * Create an instance of {@link StudyEventType }
     * 
     */
    public StudyEventType createStudyEventType() {
        return new StudyEventType();
    }

    /**
     * Create an instance of {@link StudyType }
     * 
     */
    public StudyType createStudyType() {
        return new StudyType();
    }

    /**
     * Create an instance of {@link AuditMessagesType }
     * 
     */
    public AuditMessagesType createAuditMessagesType() {
        return new AuditMessagesType();
    }

    /**
     * Create an instance of {@link SubjectGetType }
     * 
     */
    public SubjectGetType createSubjectGetType() {
        return new SubjectGetType();
    }

    /**
     * Create an instance of {@link SubjectListGetType }
     * 
     */
    public SubjectListGetType createSubjectListGetType() {
        return new SubjectListGetType();
    }

    /**
     * Create an instance of {@link EventListType }
     * 
     */
    public EventListType createEventListType() {
        return new EventListType();
    }

    /**
     * Create an instance of {@link StudyIdType }
     * 
     */
    public StudyIdType createStudyIdType() {
        return new StudyIdType();
    }

    /**
     * Create an instance of {@link CrfVersionType }
     * 
     */
    public CrfVersionType createCrfVersionType() {
        return new CrfVersionType();
    }

    /**
     * Create an instance of {@link CrfListType }
     * 
     */
    public CrfListType createCrfListType() {
        return new CrfListType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://openclinica.org/ws/dataImport/v1", name = "importDataRequest")
    public JAXBElement<Object> createImportDataRequest(Object value) {
        return new JAXBElement<Object>(_ImportDataRequest_QNAME, Object.class, null, value);
    }

}
