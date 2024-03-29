//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.24 at 11:33:30 AM MEZ 
//

package eu.esdihumboldt.hale.io.project.jaxb.generated;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for Task complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="Task">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="taskType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="taskStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contextIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@SuppressWarnings("all")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Task", propOrder = { "taskType", "comment", "taskStatus", "contextIdentifier" })
public class Task {

	@XmlElement(required = true)
	protected String taskType;
	@XmlElement(required = true)
	protected String comment;
	@XmlElement(required = true)
	protected String taskStatus;
	@XmlElement(required = true)
	protected List<String> contextIdentifier;

	/**
	 * Gets the value of the taskType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTaskType() {
		return taskType;
	}

	/**
	 * Sets the value of the taskType property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setTaskType(String value) {
		this.taskType = value;
	}

	/**
	 * Gets the value of the comment property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the value of the comment property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setComment(String value) {
		this.comment = value;
	}

	/**
	 * Gets the value of the taskStatus property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTaskStatus() {
		return taskStatus;
	}

	/**
	 * Sets the value of the taskStatus property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setTaskStatus(String value) {
		this.taskStatus = value;
	}

	/**
	 * Gets the value of the contextIdentifier property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the contextIdentifier property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getContextIdentifier().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getContextIdentifier() {
		if (contextIdentifier == null) {
			contextIdentifier = new ArrayList<String>();
		}
		return this.contextIdentifier;
	}

}
