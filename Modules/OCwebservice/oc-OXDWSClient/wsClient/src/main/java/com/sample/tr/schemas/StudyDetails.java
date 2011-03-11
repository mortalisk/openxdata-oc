
package com.sample.tr.schemas;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for StudyDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StudyDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StudyNumber" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="CreationDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="By" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Study_Identity" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StudyDetails", propOrder = {
    "studyNumber",
    "creationDate",
    "by",
    "studyIdentity"
})
public class StudyDetails {

    @XmlElement(name = "StudyNumber", required = true)
    protected BigInteger studyNumber;
    @XmlElement(name = "CreationDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar creationDate;
    @XmlElement(name = "By", required = true)
    protected String by;
    @XmlElement(name = "Study_Identity", required = true)
    protected String studyIdentity;

    /**
     * Gets the value of the studyNumber property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStudyNumber() {
        return studyNumber;
    }

    /**
     * Sets the value of the studyNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStudyNumber(BigInteger value) {
        this.studyNumber = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the by property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBy() {
        return by;
    }

    /**
     * Sets the value of the by property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBy(String value) {
        this.by = value;
    }

    /**
     * Gets the value of the studyIdentity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStudyIdentity() {
        return studyIdentity;
    }

    /**
     * Sets the value of the studyIdentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStudyIdentity(String value) {
        this.studyIdentity = value;
    }

}
