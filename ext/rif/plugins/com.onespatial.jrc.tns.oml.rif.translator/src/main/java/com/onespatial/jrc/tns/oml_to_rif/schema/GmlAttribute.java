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
 *     1Spatial PLC <http://www.1spatial.com>
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;

/**
 * A class that represents an XML attribute within a GML document.
 * 
 * @author Simon Payne (Simon.Payne@1spatial.com) / 1Spatial Group Ltd.
 * @author Richard Sunderland (Richard.Sunderland@1spatial.com) / 1Spatial Group Ltd.
 * @author Simon Templer / Fraunhofer IGD
 */
public class GmlAttribute implements Comparable<GmlAttribute>
{

    private final AttributeDefinition definition;

    /**
     * Create a GML attribute 
     * 
     * @param definition the attribute definition
     */
    public GmlAttribute(AttributeDefinition definition)
    {
        super();
        this.definition = definition;
    }

    /**
	 * @return the definition
	 */
	public AttributeDefinition getDefinition() {
		return definition;
	}

	/**
     * @return int the min occurs value
     */
    public int getMinOccurs()
    {
        return (int) definition.getMinOccurs();
    }

    /**
     * @return int the max occurs value
     */
    public int getMaxOccurs()
    {
        return (int) definition.getMaxOccurs();
    }

    /**
     * @see Comparable#compareTo(Object) which this overrides.
     * @param other
     *            {@link GmlAttribute} the one to compare with
     * @return int zero for equality, positive or negative to indicate
     *         difference
     */
    @Override
    public int compareTo(GmlAttribute other)
    {
        return definition.getName().compareTo(other.definition.getName());
    }
}
