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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaIO;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.impl.ResourceSchemaSpace;

/**
 * Loads schemas and stores them in the advisor. As such an advisor instance may
 * be used only once to load a single project.
 * 
 * @author Simon Templer
 */
public class LoadSchemaAdvisor extends AbstractIOAdvisor<SchemaReader> {

	private final Map<String, Schema> schemas = new HashMap<String, Schema>();

	private final SchemaSpaceID ssid;

	private Project project;

	/**
	 * Create an advisor for loading a schema.
	 * 
	 * @param ssid the schema space ID
	 */
	public LoadSchemaAdvisor(SchemaSpaceID ssid) {
		super();
		this.ssid = ssid;
	}

	/**
	 * @param project the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public void prepareProvider(SchemaReader provider) {
		super.prepareProvider(provider);

		provider.setSchemaSpace(ssid);

		// TODO set already loaded schemas as shared types
//			provider.setSharedTypes(...);
	}

	@Override
	public void handleResults(SchemaReader provider) {
		// add loaded schema to schema space
		schemas.put(provider.getResourceIdentifier(), provider.getSchema());

		super.handleResults(provider);
	}

	/**
	 * @return the schemas
	 */
	protected List<Schema> getSchemas() {
		return Collections.unmodifiableList(new ArrayList<>(schemas.values()));
	}

	/**
	 * Get the accumulated schemas.
	 * 
	 * @return the schema space
	 */
	public SchemaSpace getSchema() {
		// TODO cache?
		ResourceSchemaSpace dss = new ResourceSchemaSpace();

		// add all schemas
		for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
//			 load information about mapping relevant types
			SchemaIO.loadMappingRelevantTypesConfig(entry.getValue(), ssid, project);
			dss.addSchema(entry.getKey(), entry.getValue());
		}
		return dss;
	}

}