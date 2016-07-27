/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.swingrcp;

import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/

/**
 * Activator
 * 
 * @author Simon Templer
 */
public class SwingRCPPlugin extends AbstractUIPlugin {

	/**
	 * Look and feel manifest entry name
	 */
	public static final String LOOK_AND_FEEL = "LookAndFeel";

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "de.fhg.igd.swingrcp";

	private static String customLookAndFeel = null;

	// The shared instance
	private static SwingRCPPlugin plugin;

	/**
	 * @see AbstractUIPlugin#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		Enumeration<URL> manifestFiles = context.getBundle().findEntries("META-INF", "MANIFEST.MF",
				false);
		while (manifestFiles.hasMoreElements()) {
			URL manifestFile = manifestFiles.nextElement();

			Manifest manifest = new Manifest(manifestFile.openStream());
			Attributes attributes = manifest.getMainAttributes();
			String value = attributes.getValue(LOOK_AND_FEEL);

			if (value != null) {
				setCustomLookAndFeel(value.trim());
				return;
			}
		}
	}

	/**
	 * @see AbstractUIPlugin#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SwingRCPPlugin getDefault() {
		return plugin;
	}

	/**
	 * Get the custom look and feel class name
	 * 
	 * @return the custom look and feel class name
	 */
	public static String getCustomLookAndFeel() {
		return customLookAndFeel;
	}

	/**
	 * @param customLookAndFeel the customLookAndFeel to set
	 */
	public static void setCustomLookAndFeel(String customLookAndFeel) {
		SwingRCPPlugin.customLookAndFeel = customLookAndFeel;
	}

}
