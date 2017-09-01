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

import eu.esdihumboldt.hale.common.instance.extension.metadata.MetadataWorker;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.orient.storage.LocalOrientDB;
import eu.esdihumboldt.hale.common.instance.orient.storage.StoreInstancesJob;
import eu.esdihumboldt.hale.ui.DefaultReportHandler;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.service.population.PopulationService;

/**
 * Store instances in a database
 * 
 * @author Simon Templer
 */
public abstract class HaleStoreInstancesJob extends StoreInstancesJob {

	private final PopulationService ps;
	private final MetadataWorker metaworker;

	/**
	 * Create a job that stores instances in a database
	 * 
	 * @param name the (human readable) job name
	 * @param instances the instances to store in the database
	 * @param database the database
	 */
	public HaleStoreInstancesJob(String name, LocalOrientDB database,
			InstanceCollection instances) {
		super(name, database, instances, HaleUI.getServiceProvider(),
				DefaultReportHandler.getInstance(), true);

		ps = PlatformUI.getWorkbench().getService(PopulationService.class);
		metaworker = new MetadataWorker();
	}

	/**
	 * @see StoreInstancesJob#updateInstance(MutableInstance)
	 */
	@Override
	protected void updateInstance(MutableInstance instance) {
		super.updateInstance(instance);

		// generate metadata into instance
		metaworker.generate(instance);
	}

	/**
	 * @see StoreInstancesJob#processInstance(Instance)
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
			ps.addToPopulation(instance, DataSet.SOURCE);
		}
	}

}
