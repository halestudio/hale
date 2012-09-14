/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.views.tasks.model.providers.schema.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.esdihumboldt.hale.ui.service.schema.SchemaService.SchemaType;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.CombinedTaskProvider;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaSetNoGeometryTaskProvider extends CombinedTaskProvider {

	/**
	 * Default constructor
	 */
	public SchemaSetNoGeometryTaskProvider() {
		super(getTaskProviders());
	}

	private static Collection<TaskProvider> getTaskProviders() {
		List<TaskProvider> providers = new ArrayList<TaskProvider>();
		
		providers.add(new SchemaNoGeometryTaskProvider(SchemaType.SOURCE));
		providers.add(new SchemaNoGeometryTaskProvider(SchemaType.TARGET));
		
		return providers;
	}
	
}
