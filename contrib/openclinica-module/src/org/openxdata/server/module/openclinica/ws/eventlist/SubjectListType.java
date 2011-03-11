
package org.openxdata.server.module.openclinica.ws.eventlist;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.openxdata.server.module.openclinica.ws.eventlist.SubjectGetType;
import org.openxdata.server.module.openclinica.ws.eventlist.SubjectListType;


/**
 * <p>Java class for subjectListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="subjectListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="subject" type="{http://openclinica.org/ws/listbeans}subjectGetType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "subjectListType", propOrder = {
    "subject"
})
public class SubjectListType {

    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected List<SubjectGetType> subject;

    /**
     * Gets the value of the subject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SubjectGetType }
     * 
     * 
     */
    public List<SubjectGetType> getSubject() {
        if (subject == null) {
            subject = new ArrayList<SubjectGetType>();
        }
        return this.subject;
    }

}
