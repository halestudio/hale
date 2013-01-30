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

package eu.esdihumboldt.hale.common.headless;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.ServiceProvider;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Represents a loaded alignment and possible configuration for loading source
 * data and writing transformed data.
 * 
 * @author Simon Templer
 */
public interface TransformationEnvironment extends ServiceProvider {

	/**
	 * Get the unique identifier for this transformation environment.
	 * 
	 * @return the identifier
	 */
	public String getId();

	/**
	 * Get information on the associated project, if available.
	 * 
	 * @return the project information or <code>null</code>
	 */
	public ProjectInfo getProjectInfo();

	/**
	 * Get the alignment between source and target schemas.
	 * 
	 * @return the alignment
	 */
	public Alignment getAlignment();

	/**
	 * Get the source schemas.
	 * 
	 * @return the source schemas
	 */
	public SchemaSpace getSourceSchema();

	/**
	 * Get the target schemas.
	 * 
	 * @return the target schemas
	 */
	public SchemaSpace getTargetSchema();

	/**
	 * Get the export presets configured for the project.
	 * 
	 * @return copies of the export presets, fully configured except for the
	 *         target
	 */
	public Collection<? extends IOConfiguration> getExportPresets();

	/**
	 * Get export templates compatible to the target schema, not fully
	 * configured.
	 * 
	 * @return copies of the export templates, configured at least with the
	 *         action and provider ID
	 */
	public Collection<? extends IOConfiguration> getExportTemplates();

	// TODO import/export configurations

}
