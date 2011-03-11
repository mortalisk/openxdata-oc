
package org.openxdata.server.module.openclinica.ws.userlist;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.openxdata.server.module.openclinica.ws.userlist.StudyIdType;


/**
 * <p>Java class for studyIdType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="studyIdType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="studyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "studyIdType", propOrder = {
    "studyId"
})
public class StudyIdType {

    @XmlElement(namespace = "http://openclinica.org/ws/listbeans", type = Integer.class)
    protected int studyId;

    /**
     * Gets the value of the studyId property.
     * 
     */
    public int getStudyId() {
        return studyId;
    }

    /**
     * Sets the value of the studyId property.
     * 
     */
    public void setStudyId(int value) {
        this.studyId = value;
    }

}
