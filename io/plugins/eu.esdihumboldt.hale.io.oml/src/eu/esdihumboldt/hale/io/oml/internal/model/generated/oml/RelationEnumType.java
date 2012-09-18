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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for relationEnumType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="relationEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Equivalence"/>
 *     &lt;enumeration value="Subsumes"/>
 *     &lt;enumeration value="SubsumedBy"/>
 *     &lt;enumeration value="InstanceOf"/>
 *     &lt;enumeration value="HasInstance"/>
 *     &lt;enumeration value="Disjoint"/>
 *     &lt;enumeration value="PartOf"/>
 *     &lt;enumeration value="Extra"/>
 *     &lt;enumeration value="Missing"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "relationEnumType", namespace = "http://knowledgeweb.semanticweb.org/heterogeneity/alignment")
@XmlEnum
public enum RelationEnumType {

    @XmlEnumValue("Equivalence")
    EQUIVALENCE("Equivalence"),
    @XmlEnumValue("Subsumes")
    SUBSUMES("Subsumes"),
    @XmlEnumValue("SubsumedBy")
    SUBSUMED_BY("SubsumedBy"),
    @XmlEnumValue("InstanceOf")
    INSTANCE_OF("InstanceOf"),
    @XmlEnumValue("HasInstance")
    HAS_INSTANCE("HasInstance"),
    @XmlEnumValue("Disjoint")
    DISJOINT("Disjoint"),
    @XmlEnumValue("PartOf")
    PART_OF("PartOf"),
    @XmlEnumValue("Extra")
    EXTRA("Extra"),
    @XmlEnumValue("Missing")
    MISSING("Missing");
    private final String value;

    RelationEnumType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RelationEnumType fromValue(String v) {
        for (RelationEnumType c: RelationEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
