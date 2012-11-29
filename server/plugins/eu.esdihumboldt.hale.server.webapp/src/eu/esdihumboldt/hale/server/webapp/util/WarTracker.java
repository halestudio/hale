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

package eu.esdihumboldt.hale.server.webapp.util;

import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * Tracks war bundles and maintains a list of started web applications.
 * 
 * @author Michel Kraemer
 */
public class WarTracker implements BundleTrackerCustomizer<Bundle> {

	/**
	 * @see BundleTrackerCustomizer#addingBundle(Bundle, BundleEvent)
	 */
	@Override
	public Bundle addingBundle(Bundle bundle, BundleEvent event) {
		// check location
		String location = bundle.getLocation();
		if (location != null) {
			if (location.endsWith("/")) {
				location = location.substring(0, location.length() - 1);
			}
			if (location.endsWith(".war")) {
				return bundle;
			}
		}

		// check WEB-INF
		Enumeration<?> e = bundle.findEntries("/", "WEB-INF", false);
		if (e == null || !e.hasMoreElements()) {
			e = bundle.findEntries("WEB-INF", null, false);
		}
		if (e == null || !e.hasMoreElements()) {
			return null;
		}

		return bundle;
	}

	/**
	 * @see BundleTrackerCustomizer#modifiedBundle(Bundle, BundleEvent, Object)
	 */
	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Bundle object) {
		// nothing to do here
	}

	/**
	 * @see BundleTrackerCustomizer#removedBundle(Bundle, BundleEvent, Object)
	 */
	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Bundle object) {
		// nothing to do here
	}
}
