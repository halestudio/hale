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

package eu.esdihumboldt.hale.ui.io.instance;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.io.DefaultIOAdvisor;
import eu.esdihumboldt.hale.ui.io.instance.crs.DefaultCRSManager;
import eu.esdihumboldt.hale.ui.io.instance.crs.DialogCRSProvider;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advisor for importing source instances
 * 
 * @author Simon Templer
 */
public class InstanceImportAdvisor extends DefaultIOAdvisor<InstanceReader> {

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(InstanceReader provider) {
		super.prepareProvider(provider);

		SchemaService ss = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);
		provider.setSourceSchema(ss.getSchemas(SchemaSpaceID.SOURCE));
	}

	/**
	 * @see AbstractIOAdvisor#updateConfiguration(IOProvider)
	 */
	@Override
	public void updateConfiguration(InstanceReader provider) {
		super.updateConfiguration(provider);

		provider.setCRSProvider(new DefaultCRSManager(provider, new DialogCRSProvider()));
	}

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(InstanceReader provider) {
		// add instances to instance service
		InstanceService is = (InstanceService) PlatformUI.getWorkbench().getService(
				InstanceService.class);
		is.addSourceInstances(provider.getInstances());

		super.handleResults(provider);
	}

}
