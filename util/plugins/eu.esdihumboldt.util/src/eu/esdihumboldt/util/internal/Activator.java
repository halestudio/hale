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

package eu.esdihumboldt.util.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.esdihumboldt.util.reflection.OSGIPackageResolver;
import eu.esdihumboldt.util.reflection.ReflectionHelper;

/**
 * Bundle activator
 * 
 * @author Simon Templer
 */
public class Activator implements BundleActivator {

	private static BundleContext context;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;

		// register the OSGi package resolver on activation
		ReflectionHelper.setPackageResolver(new OSGIPackageResolver());
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// do nothing
	}

	/**
	 * Get the bundle context.
	 * 
	 * @return the bundle context
	 */
	public static BundleContext getContext() {
		return context;
	}

}
