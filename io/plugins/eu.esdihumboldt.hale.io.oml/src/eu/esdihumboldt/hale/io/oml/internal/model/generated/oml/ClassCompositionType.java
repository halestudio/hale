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
 * <p>Java class for ClassCompositionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassCompositionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="operator" type="{http://www.omwg.org/TR/d7/ontology/alignment}ClassOperatorType"/>
 *         &lt;choice>
 *           &lt;element name="collection" type="{http://www.omwg.org/TR/d7/ontology/alignment}ClassCollectionType"/>
 *           &lt;element ref="{http://www.omwg.org/TR/d7/ontology/alignment}FeatureClass"/>
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
@XmlType(name = "ClassCompositionType", propOrder = {
    "operator",
    "collection",
    "featureClass"
})
public class ClassCompositionType {

    @XmlElement(required = true)
    protected ClassOperatorType operator;
    protected ClassCollectionType collection;
    @XmlElement(name = "FeatureClass")
    protected ClassType featureClass;

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link ClassOperatorType }
     *     
     */
    public ClassOperatorType getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassOperatorType }
     *     
     */
    public void setOperator(ClassOperatorType value) {
        this.operator = value;
    }

    /**
     * Gets the value of the collection property.
     * 
     * @return
     *     possible object is
     *     {@link ClassCollectionType }
     *     
     */
    public ClassCollectionType getCollection() {
        return collection;
    }

    /**
     * Sets the value of the collection property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassCollectionType }
     *     
     */
    public void setCollection(ClassCollectionType value) {
        this.collection = value;
    }

    /**
     * Gets the value of the featureClass property.
     * 
     * @return
     *     possible object is
     *     {@link ClassType }
     *     
     */
    public ClassType getFeatureClass() {
        return featureClass;
    }

    /**
     * Sets the value of the featureClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassType }
     *     
     */
    public void setFeatureClass(ClassType value) {
        this.featureClass = value;
    }

}
