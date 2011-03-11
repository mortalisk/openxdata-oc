
package org.openxdata.server.module.openclinica.ws.dataimport;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.openxdata.server.module.openclinica.ws.dataimport.SubjectGetType;


/**
 * <p>Java class for subjectGetType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subjectGetType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subjectId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="personId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="studySubjectId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="secondaryId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gender" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "subjectGetType", propOrder = {
    "subjectId",
    "personId",
    "studySubjectId",
    "secondaryId",
    "gender",
    "birthDate"
})
public class SubjectGetType {

    @XmlElement(namespace = "http://openclinica.org/ws/listbeans", type = Integer.class)
    protected int subjectId;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected String personId;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected String studySubjectId;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected String secondaryId;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected String gender;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected XMLGregorianCalendar birthDate;

    /**
     * Gets the value of the subjectId property.
     * 
     */
    public int getSubjectId() {
        return subjectId;
    }

    /**
     * Sets the value of the subjectId property.
     * 
     */
    public void setSubjectId(int value) {
        this.subjectId = value;
    }

    /**
     * Gets the value of the personId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonId() {
        return personId;
    }

    /**
     * Sets the value of the personId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonId(String value) {
        this.personId = value;
    }

    /**
     * Gets the value of the studySubjectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudySubjectId() {
        return studySubjectId;
    }

    /**
     * Sets the value of the studySubjectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudySubjectId(String value) {
        this.studySubjectId = value;
    }

    /**
     * Gets the value of the secondaryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecondaryId() {
        return secondaryId;
    }

    /**
     * Sets the value of the secondaryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecondaryId(String value) {
        this.secondaryId = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGender(String value) {
        this.gender = value;
    }

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

}
