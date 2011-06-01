/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.xsd.reader.internal;

import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.RestrictionFlag;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.impl.DefaultTypeDefinition;

/**
 * XML type definition
 * @author Simon Templer
 */
public class XmlTypeDefinition extends DefaultTypeDefinition {

	/**
	 * @see DefaultTypeDefinition#DefaultTypeDefinition(QName)
	 */
	public XmlTypeDefinition(QName name) {
		super(name);
	}

	/**
	 * @see DefaultTypeDefinition#getProperties()
	 */
	@Override
	public Collection<? extends PropertyDefinition> getProperties() {
		if (getConstraint(RestrictionFlag.class).isEnabled()) {
			//XXX for restrictions assume that all properties are redefined if needed
			// only return declared properties
			return getDeclaredProperties();
		}
		
		return super.getProperties();
	}

}
