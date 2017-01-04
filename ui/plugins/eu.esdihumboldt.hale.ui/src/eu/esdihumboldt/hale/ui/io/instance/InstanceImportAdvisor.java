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

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.impl.DefaultIOAdvisor;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.io.instance.crs.DefaultCRSManager;
import eu.esdihumboldt.hale.ui.io.instance.crs.DialogCRSProvider;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.instance.sample.InstanceViewService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;

/**
 * Advisor for importing source instances
 * 
 * @author Simon Templer
 */
public class InstanceImportAdvisor extends DefaultIOAdvisor<InstanceReader> {

	private static final ALogger log = ALoggerFactory.getLogger(InstanceImportAdvisor.class);

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(InstanceReader provider) {
		super.prepareProvider(provider);

		SchemaService ss = getService(SchemaService.class);
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
		InstanceService is = getService(InstanceService.class);

		InstanceCollection instances = provider.getInstances();

		ResourceIterator<Instance> it = instances.iterator();
		try {
			if (!it.hasNext()) {
				URI loc = provider.getSource().getLocation();
				if (loc != null) {
					log.warn(MessageFormat.format(
							"No instances could be imported with the given configuration from {0}",
							loc.toString()));
				}
				else {
					log.warn("No instances could be imported with the given configuration.");
				}
			}
		} finally {
			it.close();
		}

		// apply sampling before adding to the instance service
		InstanceViewService ivs = PlatformUI.getWorkbench().getService(InstanceViewService.class);
		if (ivs != null) {
			instances = ivs.sample(instances);
		}

		is.addSourceInstances(instances);

		super.handleResults(provider);
	}

}
