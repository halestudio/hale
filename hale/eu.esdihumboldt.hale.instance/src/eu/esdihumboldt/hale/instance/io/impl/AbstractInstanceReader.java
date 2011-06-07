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

package eu.esdihumboldt.hale.instance.io.impl;

import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.instance.io.InstanceReader;
import eu.esdihumboldt.hale.schema.model.TypeIndex;

/**
 * Abstract {@link InstanceReader} base implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractInstanceReader extends AbstractImportProvider implements
		InstanceReader {
	
	private TypeIndex sourceSchema;

	/**
	 * @see InstanceReader#setSourceSchema(TypeIndex)
	 */
	@Override
	public void setSourceSchema(TypeIndex sourceSchema) {
		this.sourceSchema = sourceSchema;
	}

	/**
	 * Get the source schema
	 * 
	 * @return the source schema
	 */
	public TypeIndex getSourceSchema() {
		return sourceSchema;
	}

	/**
	 * @see AbstractImportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();
		
		if (sourceSchema == null) {
			fail("No source schema given for import");
		}
	}

}
