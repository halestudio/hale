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

package eu.esdihumboldt.hale.instanceprovider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract {@link InstanceProvider} implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractInstanceProvider implements InstanceProvider {
	
	private final Set<String> supportedSchemaFormats = new HashSet<String>();
	
	private final Set<String> supportedInstanceFormats = new HashSet<String>();
	
	/**
	 * Adds a schema format to the set of supported schema formats
	 * 
	 * @param schemaFormat the schema format
	 */
	protected void addSupportedSchemaFormat(String schemaFormat) {
		supportedSchemaFormats.add(schemaFormat);
	}
	
	/**
	 * Adds a instance format to the set of supported instance formats
	 * 
	 * @param instanceFormat the instance format
	 */
	protected void addSupportedInstanceFormat(String instanceFormat) {
		supportedInstanceFormats.add(instanceFormat);
	}
	
	/**
	 * @see InstanceProvider#getSupportedInstanceFormats()
	 */
	@Override
	public Set<? extends String> getSupportedInstanceFormats() {
		return Collections.unmodifiableSet(supportedInstanceFormats);
	}

	/**
	 * @see InstanceProvider#getSupportedSchemaFormats()
	 */
	@Override
	public Set<? extends String> getSupportedSchemaFormats() {
		return Collections.unmodifiableSet(supportedSchemaFormats);
	}

	/**
	 * @see InstanceProvider#supportsInstanceFormat(String)
	 */
	@Override
	public boolean supportsInstanceFormat(String instanceFormat) {
		return supportedInstanceFormats.contains(instanceFormat);
	}

	/**
	 * @see InstanceProvider#supportsSchemaFormat(String)
	 */
	@Override
	public boolean supportsSchemaFormat(String schemaFormat) {
		return supportedSchemaFormats.contains(schemaFormat);
	}

}
