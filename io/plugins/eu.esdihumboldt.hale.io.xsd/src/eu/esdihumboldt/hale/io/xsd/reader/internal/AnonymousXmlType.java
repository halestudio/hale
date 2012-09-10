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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;

/**
 * An anonymous XML type
 * 
 * @author Simon Templer
 */
public class AnonymousXmlType extends XmlTypeDefinition {

	/**
	 * @see XmlTypeDefinition#XmlTypeDefinition(QName)
	 */
	public AnonymousXmlType(QName name) {
		super(name);
	}

	/**
	 * @see AbstractDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		if (getSuperType() == null) {
			return "?"; //$NON-NLS-1$
		}
		else {
			return "? extends " + getSuperType().getDisplayName(); //$NON-NLS-1$
		}
	}

}
