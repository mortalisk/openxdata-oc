
package org.openxdata.server.module.openclinica.ws.dataimport;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openxdata.server.module.openclinica.ws.dataimport.CrfVersionType;


/**
 * <p>Java class for crfVersionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="crfVersionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="crfId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="oid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "crfVersionType", propOrder = {
    "crfId",
    "oid",
    "name"
})
public class CrfVersionType {

    @XmlElement(namespace = "http://openclinica.org/ws/listbeans", type = Integer.class)
    protected int crfId;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected String oid;
    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected String name;

    /**
     * Gets the value of the crfId property.
     * 
     */
    public int getCrfId() {
        return crfId;
    }

    /**
     * Sets the value of the crfId property.
     * 
     */
    public void setCrfId(int value) {
        this.crfId = value;
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

}
