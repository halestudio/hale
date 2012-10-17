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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.headless.TransformationEnvironment;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;

/**
 * Transformation environment based on a {@link Project}.
 * 
 * @author Simon Templer
 */
public class ProjectTransformationEnvironment implements TransformationEnvironment {

	private final Project project;

	private final String id;

	private final SchemaSpace sourceSchema;

	private final SchemaSpace targetSchema;

	private final Alignment alignment;

	/**
	 * Create a transformation environment based on a project file.
	 * 
	 * @param id the identifier for the transformation environment
	 * @param input the project file input
	 * @throws IOException if loading the project fails
	 */
	public ProjectTransformationEnvironment(String id,
			LocatableInputSupplier<? extends InputStream> input) throws IOException {
		super();
		this.id = id;

		// load the project
		URI location = input.getLocation();
		ProjectReader reader = HaleIO.findIOProvider(ProjectReader.class, input,
				(location != null) ? (location.getPath()) : (null));
		if (reader != null) {
			// configure reader
			reader.setSource(input);

			HeadlessProjectAdvisor advisor = new HeadlessProjectAdvisor();
			HeadlessIO.executeProvider(reader, advisor, null); // XXX
																// progress???!!
			project = advisor.getProject();
			sourceSchema = advisor.getSourceSchema();
			targetSchema = advisor.getTargetSchema();
			alignment = advisor.getAlignment();

			init(project);
		}
		else {
			throw new IOException("Cannot load project, no corresponding I/O provider found.");
		}
	}

	/**
	 * Initialize the environment based on the loaded project.
	 * 
	 * @param project the project
	 */
	protected void init(Project project) {
		// TODO import/export configurations for data
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ProjectInfo getProjectInfo() {
		return project;
	}

	@Override
	public Alignment getAlignment() {
		return alignment;
	}

	@Override
	public SchemaSpace getSourceSchema() {
		return sourceSchema;
	}

	@Override
	public SchemaSpace getTargetSchema() {
		return targetSchema;
	}

}
