/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package com.onespatial.jrc.tns.oml_to_rif.schema;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;

/**
 * A class that represents an XML attribute within a GML document.
 * 
 * @author simonp
 * @author Simon Templer
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
