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

package eu.esdihumboldt.hale.ui.transformation;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Export advisor for the transform data export using a specified instance
 * collection.
 * 
 * @author Kai Schwierczek
 */
public class TransformDataExportAdvisor extends DefaultIOAdvisor<InstanceWriter> {

	private final InstanceCollection instances;

	/**
	 * Default constructor.
	 * 
	 * @param instances the instance collection to use for the export.
	 */
	public TransformDataExportAdvisor(InstanceCollection instances) {
		this.instances = instances;

		setServiceProvider(HaleUI.getServiceProvider());
	}

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(InstanceWriter provider) {
		super.prepareProvider(provider);

		// set target schema
		SchemaService ss = getService(SchemaService.class);
		provider.setTargetSchema(ss.getSchemas(SchemaSpaceID.TARGET));

		// set instances to export
		provider.setInstances(instances);
	}
}
