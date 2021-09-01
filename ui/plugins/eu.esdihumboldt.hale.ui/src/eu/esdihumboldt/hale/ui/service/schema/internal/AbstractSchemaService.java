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

package eu.esdihumboldt.hale.ui.service.schema.internal;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.ProjectServiceAdapter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener;

/**
 * Notification handling for {@link SchemaService}s that support
 * {@link SchemaServiceListener}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractSchemaService implements SchemaService {

	private final CopyOnWriteArraySet<SchemaServiceListener> listeners = new CopyOnWriteArraySet<SchemaServiceListener>();
	private final ProjectService projectService;

	/**
	 * Create a schema service.
	 * 
	 * @param projectService the project service. The schemas will be cleared
	 *            when the project is cleaned.
	 */
	public AbstractSchemaService(ProjectService projectService) {
		super();

		this.projectService = projectService;
		projectService.addListener(new ProjectServiceAdapter() {

			@Override
			public void onClean() {
				clearSchemas(SchemaSpaceID.TARGET);
				clearSchemas(SchemaSpaceID.SOURCE);
			}

		});
	}

	/**
	 * @see SchemaService#addSchemaServiceListener(SchemaServiceListener)
	 */
	@Override
	public void addSchemaServiceListener(SchemaServiceListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see SchemaService#removeSchemaServiceListener(SchemaServiceListener)
	 */
	@Override
	public void removeSchemaServiceListener(SchemaServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Called when a schema has been added to the source or target schema space.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 * @param schema the schema that was added
	 */
	protected void notifySchemaAdded(SchemaSpaceID spaceID, Schema schema) {
		for (SchemaServiceListener listener : listeners) {
			listener.schemaAdded(spaceID, schema);
		}
	}

	/**
	 * Called when a single source or a target schema has been cleared.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 */
	protected void notifySchemaRemoved(SchemaSpaceID spaceID) {
		for (SchemaServiceListener listener : listeners) {
			listener.schemasCleared(spaceID);
		}
	}

	/**
	 * Called when the source or target schema space have been cleared.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 */
	protected void notifySchemasCleared(SchemaSpaceID spaceID) {
		for (SchemaServiceListener listener : listeners) {
			listener.schemasCleared(spaceID);
		}
	}

	/**
	 * Called when the mappable flag of some types changed.
	 * 
	 * @param spaceID the schema space of the changed types
	 * @param types the changed types
	 */
	protected void notifyMappableTypesChanged(SchemaSpaceID spaceID,
			Collection<? extends TypeDefinition> types) {
		for (SchemaServiceListener listener : listeners)
			listener.mappableTypesChanged(spaceID, types);
	}

	/**
	 * Returns the project service.
	 * 
	 * @return the project service
	 */
	protected ProjectService getProjectService() {
		return projectService;
	}
}
