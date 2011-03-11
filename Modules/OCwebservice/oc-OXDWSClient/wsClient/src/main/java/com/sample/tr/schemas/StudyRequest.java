
package com.sample.tr.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="StudyDetails" type="{http://sample.com/tr/schemas}StudyDetails"/>
 *         &lt;element name="SubjectDetails" type="{http://sample.com/tr/schemas}SubjectDetails"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "StudyRequest")
public class StudyRequest {

    @XmlElement(name = "StudyDetails", required = true)
    protected StudyDetails studyDetails;
    @XmlElement(name = "SubjectDetails", required = true)
    protected SubjectDetails subjectDetails;

    /**
     * Gets the value of the studyDetails property.
     * 
     * @return
     *     possible object is
     *     {@link StudyDetails }
     *     
     */
    public StudyDetails getStudyDetails() {
        return studyDetails;
    }

    /**
     * Sets the value of the studyDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudyDetails }
     *     
     */
    public void setStudyDetails(StudyDetails value) {
        this.studyDetails = value;
    }

    /**
     * Gets the value of the subjectDetails property.
     * 
     * @return
     *     possible object is
     *     {@link SubjectDetails }
     *     
     */
    public SubjectDetails getSubjectDetails() {
        return subjectDetails;
    }

    /**
     * Sets the value of the subjectDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubjectDetails }
     *     
     */
    public void setSubjectDetails(SubjectDetails value) {
        this.subjectDetails = value;
    }

}
