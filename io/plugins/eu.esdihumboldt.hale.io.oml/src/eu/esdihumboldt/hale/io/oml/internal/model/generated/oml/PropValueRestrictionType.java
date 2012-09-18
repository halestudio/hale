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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for propValueRestrictionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="propValueRestrictionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omwg.org/TR/d7/ontology/alignment}comparator"/>
 *         &lt;choice>
 *           &lt;element name="value" type="{http://www.omwg.org/TR/d7/ontology/alignment}valueExprType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element ref="{http://www.esdi-humboldt.eu/goml}ValueClass" minOccurs="0"/>
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
@XmlType(name = "propValueRestrictionType", propOrder = {
    "comparator",
    "value",
    "valueClass"
})
public class PropValueRestrictionType {

    @XmlElement(required = true)
    protected ComparatorEnumType comparator;
    protected List<ValueExprType> value;
    @XmlElement(name = "ValueClass", namespace = "http://www.esdi-humboldt.eu/goml")
    protected ValueClassType valueClass;

    /**
     * Gets the value of the comparator property.
     * 
     * @return
     *     possible object is
     *     {@link ComparatorEnumType }
     *     
     */
    public ComparatorEnumType getComparator() {
        return comparator;
    }

    /**
     * Sets the value of the comparator property.
     * 
     * @param value
     *     allowed object is
     *     {@link ComparatorEnumType }
     *     
     */
    public void setComparator(ComparatorEnumType value) {
        this.comparator = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the value property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueExprType }
     * 
     * 
     */
    public List<ValueExprType> getValue() {
        if (value == null) {
            value = new ArrayList<ValueExprType>();
        }
        return this.value;
    }

    /**
     * Gets the value of the valueClass property.
     * 
     * @return
     *     possible object is
     *     {@link ValueClassType }
     *     
     */
    public ValueClassType getValueClass() {
        return valueClass;
    }

    /**
     * Sets the value of the valueClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueClassType }
     *     
     */
    public void setValueClass(ValueClassType value) {
        this.valueClass = value;
    }

}
