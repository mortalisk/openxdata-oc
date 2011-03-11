
package org.openxdata.server.module.openclinica.ws.userlist;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.openxdata.server.module.openclinica.ws.userlist.EventListType;
import org.openxdata.server.module.openclinica.ws.userlist.GetUsersResponse;
import org.openxdata.server.module.openclinica.ws.userlist.ObjectFactory;
import org.openxdata.server.module.openclinica.ws.userlist.StudyEventType;
import org.openxdata.server.module.openclinica.ws.userlist.StudyIdType;
import org.openxdata.server.module.openclinica.ws.userlist.StudyListType;
import org.openxdata.server.module.openclinica.ws.userlist.StudyType;
import org.openxdata.server.module.openclinica.ws.userlist.SubjectEventType;
import org.openxdata.server.module.openclinica.ws.userlist.SubjectGetType;
import org.openxdata.server.module.openclinica.ws.userlist.SubjectListGetType;
import org.openxdata.server.module.openclinica.ws.userlist.SubjectListType;
import org.openxdata.server.module.openclinica.ws.userlist.UserType;
import org.openxdata.server.module.openclinica.ws.userlist.UsersType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openxdata.server.module.openclinica.ws.userlist package. 
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

    private final static QName _GetUsersRequest_QNAME = new QName("http://openclinica.org/ws/usersGet/v1", "getUsersRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openxdata.server.module.openclinica.ws.userlist
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StudyType }
     * 
     */
    public StudyType createStudyType() {
        return new StudyType();
    }

    /**
     * Create an instance of {@link StudyIdType }
     * 
     */
    public StudyIdType createStudyIdType() {
        return new StudyIdType();
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
     * Create an instance of {@link UserType }
     * 
     */
    public UserType createUserType() {
        return new UserType();
    }

    /**
     * Create an instance of {@link EventListType }
     * 
     */
    public EventListType createEventListType() {
        return new EventListType();
    }

    /**
     * Create an instance of {@link SubjectGetType }
     * 
     */
    public SubjectGetType createSubjectGetType() {
        return new SubjectGetType();
    }

    /**
     * Create an instance of {@link UsersType }
     * 
     */
    public UsersType createUsersType() {
        return new UsersType();
    }

    /**
     * Create an instance of {@link GetUsersResponse }
     * 
     */
    public GetUsersResponse createGetUsersResponse() {
        return new GetUsersResponse();
    }

    /**
     * Create an instance of {@link SubjectListGetType }
     * 
     */
    public SubjectListGetType createSubjectListGetType() {
        return new SubjectListGetType();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://openclinica.org/ws/usersGet/v1", name = "getUsersRequest")
    public JAXBElement<Object> createGetUsersRequest(Object value) {
        return new JAXBElement<Object>(_GetUsersRequest_QNAME, Object.class, null, value);
    }

}
