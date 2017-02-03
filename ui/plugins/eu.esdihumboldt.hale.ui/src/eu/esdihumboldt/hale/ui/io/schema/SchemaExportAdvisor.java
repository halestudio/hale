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

import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaWriter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advisor for schema export from the {@link SchemaService}
 * 
 * @author Simon Templer
 * @since 2.7
 */
public class SchemaExportAdvisor extends DefaultIOAdvisor<SchemaWriter> {

	private final SchemaSpaceID spaceID;

	/**
	 * Create a schema export advisor
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 */
	public SchemaExportAdvisor(SchemaSpaceID spaceID) {
		super();
		this.spaceID = spaceID;
	}

	@Override
	public void prepareProvider(SchemaWriter provider) {
		super.prepareProvider(provider);

		SchemaService ss = getService(SchemaService.class);
		provider.setSchemas(ss.getSchemas(spaceID));
	}

}
