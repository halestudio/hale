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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.security.user.simple.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.fhg.igd.osgi.util.AbstractBundleActivator;

/**
 * The bundle activator.
 * 
 * @author Simon Templer
 */
public class Activator extends AbstractBundleActivator {

	private static Activator instance;

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
	}

	/**
	 * @see AbstractBundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		instance = this;
	}

	/**
	 * @return the instance
	 */
	public static Activator getInstance() {
		return instance;
	}

}
