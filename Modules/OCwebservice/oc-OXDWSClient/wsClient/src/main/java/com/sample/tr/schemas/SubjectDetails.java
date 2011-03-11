
package com.sample.tr.schemas;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SubjectDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubjectDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NumberOfSubjects" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="MasterSubject" type="{http://sample.com/tr/schemas}MasterSubject"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubjectDetails", propOrder = {
    "numberOfSubjects",
    "masterSubject"
})
public class SubjectDetails {

    @XmlElement(name = "NumberOfSubjects", required = true)
    protected BigInteger numberOfSubjects;
    @XmlElement(name = "MasterSubject", required = true)
    protected MasterSubject masterSubject;

    /**
     * Gets the value of the numberOfSubjects property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfSubjects() {
        return numberOfSubjects;
    }

    /**
     * Sets the value of the numberOfSubjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfSubjects(BigInteger value) {
        this.numberOfSubjects = value;
    }

    /**
     * Gets the value of the masterSubject property.
     * 
     * @return
     *     possible object is
     *     {@link MasterSubject }
     *     
     */
    public MasterSubject getMasterSubject() {
        return masterSubject;
    }

    /**
     * Sets the value of the masterSubject property.
     * 
     * @param value
     *     allowed object is
     *     {@link MasterSubject }
     *     
     */
    public void setMasterSubject(MasterSubject value) {
        this.masterSubject = value;
    }

}
