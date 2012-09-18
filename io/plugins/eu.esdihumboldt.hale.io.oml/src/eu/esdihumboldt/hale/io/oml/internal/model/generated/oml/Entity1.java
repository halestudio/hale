/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */


package eu.esdihumboldt.hale.io.oml.internal.model.generated.oml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omwg.org/TR/d7/ontology/alignment}_Entity"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "entity"
})
@XmlRootElement(name = "entity1")
public class Entity1 {

    @XmlElementRef(name = "_Entity", namespace = "http://www.omwg.org/TR/d7/ontology/alignment", type = JAXBElement.class)
    protected JAXBElement<? extends EntityType> entity;

    /**
     * Gets the value of the entity property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link RelationType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EntityType }{@code >}
     *     {@link JAXBElement }{@code <}{@link InstanceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyQualifierType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassType }{@code >}
     *     
     */
    public JAXBElement<? extends EntityType> getEntity() {
        return entity;
    }

    /**
     * Sets the value of the entity property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link RelationType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassType }{@code >}
     *     {@link JAXBElement }{@code <}{@link EntityType }{@code >}
     *     {@link JAXBElement }{@code <}{@link InstanceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link PropertyQualifierType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ClassType }{@code >}
     *     
     */
    public void setEntity(JAXBElement<? extends EntityType> value) {
        this.entity = ((JAXBElement<? extends EntityType> ) value);
    }

}
