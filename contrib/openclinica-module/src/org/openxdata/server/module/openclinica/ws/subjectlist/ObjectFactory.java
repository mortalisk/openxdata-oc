
package org.openxdata.server.module.openclinica.ws.subjectlist;

import javax.xml.bind.annotation.XmlRegistry;
import org.openxdata.server.module.openclinica.ws.subjectlist.EventListType;
import org.openxdata.server.module.openclinica.ws.subjectlist.GetSubjectsRequest;
import org.openxdata.server.module.openclinica.ws.subjectlist.GetSubjectsResponse;
import org.openxdata.server.module.openclinica.ws.subjectlist.ObjectFactory;
import org.openxdata.server.module.openclinica.ws.subjectlist.StudyEventType;
import org.openxdata.server.module.openclinica.ws.subjectlist.StudyIdType;
import org.openxdata.server.module.openclinica.ws.subjectlist.StudyListType;
import org.openxdata.server.module.openclinica.ws.subjectlist.StudyType;
import org.openxdata.server.module.openclinica.ws.subjectlist.SubjectEventType;
import org.openxdata.server.module.openclinica.ws.subjectlist.SubjectGetType;
import org.openxdata.server.module.openclinica.ws.subjectlist.SubjectListGetType;
import org.openxdata.server.module.openclinica.ws.subjectlist.SubjectListType;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.openxdata.server.module.openclinica.ws.subjectlist package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.openxdata.server.module.openclinica.ws.subjectlist
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StudyListType }
     * 
     */
    public StudyListType createStudyListType() {
        return new StudyListType();
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
     * Create an instance of {@link GetSubjectsResponse }
     * 
     */
    public GetSubjectsResponse createGetSubjectsResponse() {
        return new GetSubjectsResponse();
    }

    /**
     * Create an instance of {@link GetSubjectsRequest }
     * 
     */
    public GetSubjectsRequest createGetSubjectsRequest() {
        return new GetSubjectsRequest();
    }

    /**
     * Create an instance of {@link StudyType }
     * 
     */
    public StudyType createStudyType() {
        return new StudyType();
    }

    /**
     * Create an instance of {@link SubjectGetType }
     * 
     */
    public SubjectGetType createSubjectGetType() {
        return new SubjectGetType();
    }

    /**
     * Create an instance of {@link EventListType }
     * 
     */
    public EventListType createEventListType() {
        return new EventListType();
    }

    /**
     * Create an instance of {@link SubjectListType }
     * 
     */
    public SubjectListType createSubjectListType() {
        return new SubjectListType();
    }

    /**
     * Create an instance of {@link StudyIdType }
     * 
     */
    public StudyIdType createStudyIdType() {
        return new StudyIdType();
    }

    /**
     * Create an instance of {@link StudyEventType }
     * 
     */
    public StudyEventType createStudyEventType() {
        return new StudyEventType();
    }

}
