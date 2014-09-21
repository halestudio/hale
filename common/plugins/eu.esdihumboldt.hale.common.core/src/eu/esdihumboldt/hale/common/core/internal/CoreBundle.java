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

package eu.esdihumboldt.hale.common.core.internal;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.esdihumboldt.hale.common.core.service.cleanup.CleanupService;
import eu.esdihumboldt.hale.common.core.service.cleanup.impl.CleanupServiceImpl;

/**
 * Bundle activator
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public class CoreBundle implements BundleActivator {

	private BundleContext context;

	private CleanupServiceImpl cleanupService;

	private ServiceRegistration<CleanupService> cleanupServiceRef;

	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;

		cleanupService = new CleanupServiceImpl();
		cleanupServiceRef = context.registerService(CleanupService.class, cleanupService,
				new Hashtable<String, Object>());
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		cleanupServiceRef.unregister();
		cleanupService.triggerApplicationCleanup();
		cleanupService = null;

		this.context = null;
	}

	/**
	 * @return the context
	 */
	public BundleContext getContext() {
		return context;
	}

}
