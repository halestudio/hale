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

package eu.esdihumboldt.hale.common.schema.io.impl;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.impl.ConfigurationIOAdvisor;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;

/**
 * I/O configuration based advisor for loading schemas, that collects loaded
 * schemas in a {@link SchemaSpace}.
 * @author Simon Templer
 */
public class LoadSchemaAdvisor extends ConfigurationIOAdvisor<SchemaReader> {

	private final DefaultSchemaSpace schemaSpace = new DefaultSchemaSpace();
	
	/**
	 * @see AbstractIOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(SchemaReader provider) {
		schemaSpace.addSchema(provider.getSchema());
	}

	/**
	 * Get the schema space with the schemas loaded using this advisor.
	 * @return the schema space with the collected schemas
	 */
	public SchemaSpace getSchemaSpace() {
		return schemaSpace;
	}

}
