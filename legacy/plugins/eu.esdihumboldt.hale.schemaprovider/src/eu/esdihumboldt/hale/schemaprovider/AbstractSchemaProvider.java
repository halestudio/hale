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

package eu.esdihumboldt.hale.schemaprovider;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract {@link SchemaProvider} managing the supported schema formats
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public abstract class AbstractSchemaProvider implements SchemaProvider {

	private final Set<String> supportedFormats = new HashSet<String>();

	/**
	 * @see SchemaProvider#supportsSchemaFormat(String)
	 */
	@Override
	public boolean supportsSchemaFormat(String schemaFormat) {
		return supportedFormats.contains(schemaFormat.toLowerCase());
	}

	/**
	 * Add a supported format
	 * 
	 * @param format
	 *            the supported format to add
	 */
	public void addSupportedFormat(String format) {
		supportedFormats.add(format.toLowerCase());
	}

	/**
	 * @see SchemaProvider#getSupportedSchemaFormats()
	 */
	@Override
	public Collection<? extends String> getSupportedSchemaFormats() {
		return new HashSet<String>(supportedFormats);
	}

}
