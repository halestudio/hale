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
