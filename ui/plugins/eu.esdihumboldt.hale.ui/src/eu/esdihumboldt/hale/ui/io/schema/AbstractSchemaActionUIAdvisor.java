/*
 * Copyright (c) 2021 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.io.schema;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.ui.io.action.AbstractActionUIAdvisor;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Base implementation for schema action UI advisors.
 * 
 * @param <T> the resource representation type
 * 
 * @author Kapil Agnihotri.
 */
public abstract class AbstractSchemaActionUIAdvisor<T> extends AbstractActionUIAdvisor<Schema> {

	/**
	 * Schema service field for currently loaded schemas.
	 */
	protected final SchemaService schemaService;

	/**
	 * Instance service field for different data sets.
	 */
	protected InstanceService instanceService;

	/**
	 * Alignment service field for currently loaded alignment.
	 */
	protected AlignmentService alignmentService;

	/**
	 * Constructor.
	 */
	public AbstractSchemaActionUIAdvisor() {
		super();
		this.schemaService = PlatformUI.getWorkbench().getService(SchemaService.class);
		this.instanceService = PlatformUI.getWorkbench().getService(InstanceService.class);;
		this.alignmentService = PlatformUI.getWorkbench().getService(AlignmentService.class);
	}

	/**
	 * Constructor.
	 * 
	 * @param schemaService schema service.
	 * @param instanceService instance service.
	 * @param alignmentService alignment service.
	 */
	public AbstractSchemaActionUIAdvisor(SchemaService schemaService,
			InstanceService instanceService, AlignmentService alignmentService) {
		super();
		this.schemaService = schemaService;
		this.instanceService = instanceService;
		this.alignmentService = alignmentService;
	}

	/**
	 * Method to remove the specified resource.
	 * 
	 * @param resourceId the resource identifier
	 * @param schemaSpaceId schema space identifier.
	 * @return true if the schema was removed, false otherwise.
	 */
	public boolean removeResource(String resourceId, SchemaSpaceID schemaSpaceId) {
		return schemaService.removeSchema(resourceId, schemaSpaceId);
	}

	@Override
	public boolean supportsRetrieval() {
		return true;
	}

	@Override
	public Class<Schema> getRepresentationType() {
		return Schema.class;
	}

	/**
	 * Method to retrieve the specified schema based on resource ID.
	 * 
	 * @param resourceId the resource identifier.
	 * @param schemaSpaceId schema space identifier.
	 * @return schema.
	 */
	public Schema retrieveResource(String resourceId, SchemaSpaceID schemaSpaceId) {
		return schemaService.getSchema(resourceId, schemaSpaceId);
	}

}
