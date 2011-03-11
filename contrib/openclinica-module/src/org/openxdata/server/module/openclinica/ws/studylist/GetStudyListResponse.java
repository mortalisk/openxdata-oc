
package org.openxdata.server.module.openclinica.ws.studylist;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getStudyListResponse element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="getStudyListResponse">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="result" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="label" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="warning" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="studyList" type="{http://openclinica.org/ws/listbeans}studyListType" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "", propOrder = {
    "result",
    "label",
    "warning",
    "error",
    "studyList"
})
@XmlRootElement(name = "getStudyListResponse", namespace = "http://openclinica.org/ws/studyListGet/v1")
public class GetStudyListResponse {

    @XmlElement(namespace = "http://openclinica.org/ws/studyListGet/v1")
    protected String result;
    @XmlElement(namespace = "http://openclinica.org/ws/studyListGet/v1")
    protected String label;
    @XmlElement(namespace = "http://openclinica.org/ws/studyListGet/v1")
    protected List<String> warning;
    @XmlElement(namespace = "http://openclinica.org/ws/studyListGet/v1")
    protected List<String> error;
    @XmlElement(namespace = "http://openclinica.org/ws/studyListGet/v1")
    protected StudyListType studyList;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResult(String value) {
        this.result = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the warning property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the warning property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWarning().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getWarning() {
        if (warning == null) {
            warning = new ArrayList<String>();
        }
        return this.warning;
    }

    /**
     * Gets the value of the error property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the error property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getError().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getError() {
        if (error == null) {
            error = new ArrayList<String>();
        }
        return this.error;
    }

    /**
     * Gets the value of the studyList property.
     * 
     * @return
     *     possible object is
     *     {@link StudyListType }
     *     
     */
    public StudyListType getStudyList() {
        return studyList;
    }

    /**
     * Sets the value of the studyList property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudyListType }
     *     
     */
    public void setStudyList(StudyListType value) {
        this.studyList = value;
    }

}
