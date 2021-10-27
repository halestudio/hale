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

package eu.esdihumboldt.hale.ui.io.schema;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advisor for schema import to the {@link SchemaService}
 * 
 * @author Simon Templer
 * @since 2.5
 */
public class SchemaImportAdvisor extends DefaultIOAdvisor<SchemaReader> {

	private final SchemaSpaceID spaceID;

	/**
	 * Create a schema import advisor
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 */
	public SchemaImportAdvisor(SchemaSpaceID spaceID) {
		super();
		this.spaceID = spaceID;
	}

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(SchemaReader provider) {
		super.prepareProvider(provider);

		provider.setSchemaSpace(spaceID);

		// set shared types XXX this is not fixed yet
		SchemaService ss = getService(SchemaService.class);
		provider.setSharedTypes(ss.getSchemas(spaceID));
	}

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(SchemaReader provider) {
		// add loaded schema to schema space
		Schema schema = provider.getSchema();

		SchemaService ss = getService(SchemaService.class);
		ss.addSchema(provider.getResourceIdentifier(), schema, spaceID);

		if (ss.getSchemas(spaceID).getMappingRelevantTypes().isEmpty()) {
			// if no types are present after loading, open editor for mapping
			// relevant types
			ss.editMappableTypes(spaceID);
		}

		super.handleResults(provider);
	}

}
