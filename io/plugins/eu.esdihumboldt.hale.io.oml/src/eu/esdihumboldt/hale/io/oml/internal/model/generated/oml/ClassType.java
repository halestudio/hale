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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClassType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClassType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omwg.org/TR/d7/ontology/alignment}EntityType">
 *       &lt;sequence>
 *         &lt;element name="classComposition" type="{http://www.omwg.org/TR/d7/ontology/alignment}ClassCompositionType" minOccurs="0"/>
 *         &lt;element name="attributeValueCondition" type="{http://www.omwg.org/TR/d7/ontology/alignment}ClassConditionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="attributeTypeCondition" type="{http://www.omwg.org/TR/d7/ontology/alignment}ClassConditionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="attributeOccurenceCondition" type="{http://www.omwg.org/TR/d7/ontology/alignment}ClassConditionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClassType", propOrder = {
    "classComposition",
    "attributeValueCondition",
    "attributeTypeCondition",
    "attributeOccurenceCondition"
})
public class ClassType
    extends EntityType
{

    protected ClassCompositionType classComposition;
    protected List<ClassConditionType> attributeValueCondition;
    protected List<ClassConditionType> attributeTypeCondition;
    protected List<ClassConditionType> attributeOccurenceCondition;

    /**
     * Gets the value of the classComposition property.
     * 
     * @return
     *     possible object is
     *     {@link ClassCompositionType }
     *     
     */
    public ClassCompositionType getClassComposition() {
        return classComposition;
    }

    /**
     * Sets the value of the classComposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassCompositionType }
     *     
     */
    public void setClassComposition(ClassCompositionType value) {
        this.classComposition = value;
    }

    /**
     * Gets the value of the attributeValueCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeValueCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeValueCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassConditionType }
     * 
     * 
     */
    public List<ClassConditionType> getAttributeValueCondition() {
        if (attributeValueCondition == null) {
            attributeValueCondition = new ArrayList<ClassConditionType>();
        }
        return this.attributeValueCondition;
    }

    /**
     * Gets the value of the attributeTypeCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeTypeCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeTypeCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassConditionType }
     * 
     * 
     */
    public List<ClassConditionType> getAttributeTypeCondition() {
        if (attributeTypeCondition == null) {
            attributeTypeCondition = new ArrayList<ClassConditionType>();
        }
        return this.attributeTypeCondition;
    }

    /**
     * Gets the value of the attributeOccurenceCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeOccurenceCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeOccurenceCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClassConditionType }
     * 
     * 
     */
    public List<ClassConditionType> getAttributeOccurenceCondition() {
        if (attributeOccurenceCondition == null) {
            attributeOccurenceCondition = new ArrayList<ClassConditionType>();
        }
        return this.attributeOccurenceCondition;
    }

}
