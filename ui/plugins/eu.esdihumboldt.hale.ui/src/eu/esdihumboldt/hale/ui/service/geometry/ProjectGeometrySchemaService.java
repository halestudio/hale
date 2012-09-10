/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
