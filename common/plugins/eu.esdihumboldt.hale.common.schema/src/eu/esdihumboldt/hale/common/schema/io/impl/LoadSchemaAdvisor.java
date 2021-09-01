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

package eu.esdihumboldt.hale.common.schema.io.impl;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.impl.ConfigurationIOAdvisor;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.impl.ResourceSchemaSpace;

/**
 * I/O configuration based advisor for loading schemas, that collects loaded
 * schemas in a {@link SchemaSpace}.
 * 
 * @author Simon Templer
 */
public class LoadSchemaAdvisor extends ConfigurationIOAdvisor<SchemaReader> {

	private final ResourceSchemaSpace schemaSpace = new ResourceSchemaSpace();
	private final SchemaSpaceID ssid;

	/**
	 * Constructor.
	 * 
	 * @param ssid the associated schema space id
	 */
	public LoadSchemaAdvisor(SchemaSpaceID ssid) {
		this.ssid = ssid;
	}

	@Override
	public void prepareProvider(SchemaReader provider) {
		super.prepareProvider(provider);

		provider.setSchemaSpace(ssid);
	}

	/**
	 * @see AbstractIOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(SchemaReader provider) {
		schemaSpace.addSchema(provider.getResourceIdentifier(), provider.getSchema());
	}

	/**
	 * Get the schema space with the schemas loaded using this advisor.
	 * 
	 * @return the schema space with the collected schemas
	 */
	public SchemaSpace getSchemaSpace() {
		return schemaSpace;
	}

}
