
package org.openxdata.server.module.openclinica.ws.dataimport;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openxdata.server.module.openclinica.ws.dataimport.CrfListType;
import org.openxdata.server.module.openclinica.ws.dataimport.StudyEventType;


/**
 * <p>Java class for studyEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studyEventType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="oid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="crfList" type="{http://openclinica.org/ws/listbeans}crfListType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "studyEventType", propOrder = {
    "eventId",
    "oid",
    "name",
    "crfList"
})
public class StudyEventType {

    @XmlElement(namespace = "http://openclinica.org/ws/listbeans", type = Integer.class)
    protected int eventId;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected String oid;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected String name;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected CrfListType crfList;

    /**
     * Gets the value of the eventId property.
     * 
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     * 
     */
    public void setEventId(int value) {
        this.eventId = value;
    }

    /**
     * Gets the value of the oid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOid() {
        return oid;
    }

    /**
     * Sets the value of the oid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOid(String value) {
        this.oid = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the crfList property.
     * 
     * @return
     *     possible object is
     *     {@link CrfListType }
     *     
     */
    public CrfListType getCrfList() {
        return crfList;
    }

    /**
     * Sets the value of the crfList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CrfListType }
     *     
     */
    public void setCrfList(CrfListType value) {
        this.crfList = value;
    }

}
