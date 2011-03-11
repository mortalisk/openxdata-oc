
package com.sample.tr.schemas;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.sample.tr.schemas package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sample.tr.schemas
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StudyDetails }
     * 
     */
    public StudyDetails createStudyDetails() {
        return new StudyDetails();
    }

    /**
     * Create an instance of {@link StudyResponse }
     * 
     */
    public StudyResponse createStudyResponse() {
        return new StudyResponse();
    }

    /**
     * Create an instance of {@link MasterSubject }
     * 
     */
    public MasterSubject createMasterSubject() {
        return new MasterSubject();
    }

    /**
     * Create an instance of {@link SubjectDetails }
     * 
     */
    public SubjectDetails createSubjectDetails() {
        return new SubjectDetails();
    }

    /**
     * Create an instance of {@link StudyRequest }
     * 
     */
    public StudyRequest createStudyRequest() {
        return new StudyRequest();
    }

}
