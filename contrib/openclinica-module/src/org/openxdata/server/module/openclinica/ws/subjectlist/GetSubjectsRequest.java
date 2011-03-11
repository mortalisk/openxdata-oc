
package org.openxdata.server.module.openclinica.ws.subjectlist;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openxdata.server.module.openclinica.ws.subjectlist.GetSubjectsRequest;


/**
 * <p>Java class for getSubjectsRequest element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="getSubjectsRequest">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="studyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "studyId"
})
@XmlRootElement(name = "getSubjectsRequest", namespace = "http://openclinica.org/ws/subjectListGet/v1")
public class GetSubjectsRequest {

    @XmlElement(namespace = "http://openclinica.org/ws/subjectListGet/v1", type = Integer.class)
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
