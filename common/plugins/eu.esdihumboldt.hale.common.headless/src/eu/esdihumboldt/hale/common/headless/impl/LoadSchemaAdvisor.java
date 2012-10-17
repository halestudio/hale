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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;

/**
 * Loads schemas and stores them in the advisor. As such an advisor instance may
 * be used only once to load a single project.
 * 
 * @author Simon Templer
 */
class LoadSchemaAdvisor extends AbstractIOAdvisor<SchemaReader> {

	private final List<Schema> schemas = new ArrayList<Schema>();

	@Override
	public void prepareProvider(SchemaReader provider) {
		super.prepareProvider(provider);

		// TODO set already loaded schemas as shared types
//			provider.setSharedTypes(...);
	}

	@Override
	public void handleResults(SchemaReader provider) {
		// add loaded schema to schema space
		schemas.add(provider.getSchema());

		super.handleResults(provider);
	}

	/**
	 * @return the schemas
	 */
	protected List<Schema> getSchemas() {
		return Collections.unmodifiableList(schemas);
	}

	/**
	 * Get the accumulated schemas.
	 * 
	 * @return the schema space
	 */
	public SchemaSpace getSchema() {
		// TODO cache?
		DefaultSchemaSpace dss = new DefaultSchemaSpace();

		/*
		 * XXX what about settings for mapping relevant types??? they are
		 * important for the data loading. See
		 * HeadleassProjectAdvisor.handleResults
		 */

		// add all schemas
		for (Schema schema : schemas) {
			dss.addSchema(schema);
		}

		return dss;
	}

}