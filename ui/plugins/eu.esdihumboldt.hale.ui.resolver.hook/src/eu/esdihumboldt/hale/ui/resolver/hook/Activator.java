/*
 * Copyright (c) 2026 wetransform GmbH
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.esdihumboldt.hale.ui.resolver.hook;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.resolver.ResolverHookFactory;

/**
 * Registers the {@link MetaInfDynamicImportHook}. The bundle is started at
 * start level 1, before bundles are resolved dynamically.
 */
public class Activator implements BundleActivator {

	private ServiceRegistration<ResolverHookFactory> registration;

	@Override
	public void start(BundleContext context) throws Exception {
		registration = context.registerService(ResolverHookFactory.class,
				triggers -> new MetaInfDynamicImportHook(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (registration != null) {
			registration.unregister();
			registration = null;
		}
	}

}
