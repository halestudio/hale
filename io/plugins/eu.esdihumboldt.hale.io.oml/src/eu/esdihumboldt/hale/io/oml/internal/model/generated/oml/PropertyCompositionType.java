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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PropertyCompositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyCompositionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operator" type="{http://www.omwg.org/TR/d7/ontology/alignment}PropertyOperatorType"/>
 *         &lt;choice>
 *           &lt;element name="collection" type="{http://www.omwg.org/TR/d7/ontology/alignment}PropertyCollectionType"/>
 *           &lt;element ref="{http://www.omwg.org/TR/d7/ontology/alignment}Property"/>
 *           &lt;element ref="{http://www.omwg.org/TR/d7/ontology/alignment}Relation"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PropertyCompositionType", propOrder = {
    "operator",
    "collection",
    "property",
    "relation"
})
public class PropertyCompositionType {

    @XmlElement(required = true)
    protected PropertyOperatorType operator;
    protected PropertyCollectionType collection;
    @XmlElement(name = "Property")
    protected PropertyType property;
    @XmlElement(name = "Relation")
    protected RelationType relation;

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyOperatorType }
     *     
     */
    public PropertyOperatorType getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyOperatorType }
     *     
     */
    public void setOperator(PropertyOperatorType value) {
        this.operator = value;
    }

    /**
     * Gets the value of the collection property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyCollectionType }
     *     
     */
    public PropertyCollectionType getCollection() {
        return collection;
    }

    /**
     * Sets the value of the collection property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyCollectionType }
     *     
     */
    public void setCollection(PropertyCollectionType value) {
        this.collection = value;
    }

    /**
     * Gets the value of the property property.
     * 
     * @return
     *     possible object is
     *     {@link PropertyType }
     *     
     */
    public PropertyType getProperty() {
        return property;
    }

    /**
     * Sets the value of the property property.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyType }
     *     
     */
    public void setProperty(PropertyType value) {
        this.property = value;
    }

    /**
     * Gets the value of the relation property.
     * 
     * @return
     *     possible object is
     *     {@link RelationType }
     *     
     */
    public RelationType getRelation() {
        return relation;
    }

    /**
     * Sets the value of the relation property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationType }
     *     
     */
    public void setRelation(RelationType value) {
        this.relation = value;
    }

}
