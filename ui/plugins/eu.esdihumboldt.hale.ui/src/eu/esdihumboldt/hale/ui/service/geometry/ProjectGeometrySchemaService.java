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

package eu.esdihumboldt.hale.ui.service.geometry;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.geometry.service.impl.AbstractGeometrySchemaService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Geometry schema service that stores the default geometry information in the
 * project.
 * 
 * @author Simon Templer
 */
public class ProjectGeometrySchemaService extends AbstractGeometrySchemaService {

	private static final String KEY_PREFIX = "defaultGeometry:";

	private final ProjectService projectService;

	/**
	 * Create a geometry schema service storing the default geometry information
	 * using the given project service.
	 * 
	 * @param projectService the project service
	 */
	public ProjectGeometrySchemaService(ProjectService projectService) {
		super();

		this.projectService = projectService;
	}

	/**
	 * @see AbstractGeometrySchemaService#loadDefaultGeometry(TypeDefinition)
	 */
	@Override
	protected List<QName> loadDefaultGeometry(TypeDefinition type) {
		List<String> names = projectService.getConfigurationService().getList(
				KEY_PREFIX + type.getName().toString());

		if (names == null) {
			return null;
		}
		else {
			// create QNames from strings
			List<QName> qnames = new ArrayList<QName>(names.size());
			for (String name : names) {
				qnames.add(QName.valueOf(name));
			}
			return qnames;
		}
	}

	/**
	 * @see AbstractGeometrySchemaService#saveDefaultGeometry(TypeDefinition,
	 *      List)
	 */
	@Override
	protected void saveDefaultGeometry(TypeDefinition type, List<QName> path) {
		List<String> names;
		if (path == null) {
			names = null;
		}
		else {
			// create strings from QNames
			names = new ArrayList<String>(path.size());
			for (QName name : path) {
				names.add(name.toString());
			}
		}

		// FIXME it may be a problem that setList treats an empty list and null
		// the same
		projectService.getConfigurationService().setList(KEY_PREFIX + type.getName().toString(),
				names);
	}

}
