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

package eu.esdihumboldt.hale.ui.service.instance.internal.orient;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.orient.storage.LocalOrientDB;
import eu.esdihumboldt.hale.common.instance.orient.storage.OrientInstanceSink;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;

/**
 * Instance sink based on a {@link LocalOrientDB}
 * 
 * @author Simon Templer
 */
public class HaleOrientInstanceSink extends OrientInstanceSink {

	private final PopulationService ps;

	/**
	 * Create an instance sink based on a {@link LocalOrientDB}
	 * 
	 * @param database the sink database
	 * @param lockNow if the database should be locked now
	 */
	public HaleOrientInstanceSink(LocalOrientDB database, boolean lockNow) {
		super(database, lockNow);

		ps = PlatformUI.getWorkbench().getService(PopulationService.class);
	}

	/**
	 * @see OrientInstanceSink#processInstance(Instance)
	 */
	@Override
	protected void processInstance(Instance instance) {
		super.processInstance(instance);

		// population count
		/*
		 * XXX This is done here because otherwise the whole data set would have
		 * again to be retrieved from the database. See PopulationServiceImpl
		 */
		if (ps != null) {
			ps.addToPopulation(instance, DataSet.TRANSFORMED);
		}
	}

}
