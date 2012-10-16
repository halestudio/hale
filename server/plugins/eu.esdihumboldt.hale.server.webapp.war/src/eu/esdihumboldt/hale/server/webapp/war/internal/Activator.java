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

package eu.esdihumboldt.hale.server.webapp.war.internal;

import java.util.Arrays;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTracker;

import eu.esdihumboldt.hale.server.webapp.util.WarTracker;

/**
 * The bundle activator.
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
public class Activator implements BundleActivator {

	/**
	 * The singleton instance of this activator
	 */
	private static Activator instance;

	/**
	 * The bundle context
	 */
	private BundleContext context;

	/**
	 * Tracks war bundles
	 */
	private BundleTracker<Bundle> warTracker;

	/**
	 * @return the singleton instance of this activator
	 */
	public static Activator getInstance() {
		return instance;
	}

	/**
	 * Get the bundle context
	 * 
	 * @return the bundle context
	 */
	public BundleContext getContext() {
		return context;
	}

	/**
	 * @see BundleActivator#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		this.context = context;

		// track war bundles
		warTracker = new BundleTracker<Bundle>(context, Bundle.ACTIVE, new WarTracker());
		warTracker.open();
	}

	/**
	 * @see BundleActivator#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		warTracker.close();
	}

	/**
	 * @return all war bundles currently started
	 */
	public List<Bundle> getWarBundles() {
		return Arrays.asList(warTracker.getBundles());
	}

}
