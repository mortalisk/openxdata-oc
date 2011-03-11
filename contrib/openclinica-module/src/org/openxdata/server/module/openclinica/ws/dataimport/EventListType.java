
package org.openxdata.server.module.openclinica.ws.dataimport;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openxdata.server.module.openclinica.ws.dataimport.EventListType;
import org.openxdata.server.module.openclinica.ws.dataimport.StudyEventType;


/**
 * <p>Java class for eventListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="event" type="{http://openclinica.org/ws/listbeans}studyEventType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "eventListType", propOrder = {
    "event"
})
public class EventListType {

    @XmlElement(namespace = "http://openclinica.org/ws/listbeans")
    protected List<StudyEventType> event;

    /**
     * Gets the value of the event property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the event property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StudyEventType }
     * 
     * 
     */
    public List<StudyEventType> getEvent() {
        if (event == null) {
            event = new ArrayList<StudyEventType>();
        }
        return this.event;
    }

}
