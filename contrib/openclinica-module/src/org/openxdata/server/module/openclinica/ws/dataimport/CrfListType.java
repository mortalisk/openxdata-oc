
package org.openxdata.server.module.openclinica.ws.dataimport;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openxdata.server.module.openclinica.ws.dataimport.CrfListType;
import org.openxdata.server.module.openclinica.ws.dataimport.CrfVersionType;


/**
 * <p>Java class for crfListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="crfListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="crf" type="{http://openclinica.org/ws/listbeans}crfVersionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "crfListType", propOrder = {
    "crf"
})
public class CrfListType {

    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected List<CrfVersionType> crf;

    /**
     * Gets the value of the crf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the crf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCrf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CrfVersionType }
     * 
     * 
     */
    public List<CrfVersionType> getCrf() {
        if (crf == null) {
            crf = new ArrayList<CrfVersionType>();
        }
        return this.crf;
    }

}
