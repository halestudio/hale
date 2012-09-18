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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RelationOperatorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RelationOperatorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INTERSECTION"/>
 *     &lt;enumeration value="UNION"/>
 *     &lt;enumeration value="UNION_DUPLICATES"/>
 *     &lt;enumeration value="COMPLEMENT"/>
 *     &lt;enumeration value="INVERSE"/>
 *     &lt;enumeration value="SYMMETRIC"/>
 *     &lt;enumeration value="TRANSITIVE"/>
 *     &lt;enumeration value="REFLEXIVE"/>
 *     &lt;enumeration value="FIRST"/>
 *     &lt;enumeration value="NEXT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RelationOperatorType")
@XmlEnum
public enum RelationOperatorType {

    INTERSECTION,
    UNION,
    UNION_DUPLICATES,
    COMPLEMENT,
    INVERSE,
    SYMMETRIC,
    TRANSITIVE,
    REFLEXIVE,
    FIRST,
    NEXT;

    public String value() {
        return name();
    }

    public static RelationOperatorType fromValue(String v) {
        return valueOf(v);
    }

}
