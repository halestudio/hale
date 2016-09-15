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
package eu.esdihumboldt.hale.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import eu.esdihumboldt.hale.ui.service.instance.internal.orient.OrientInstanceService;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * The activator class controls the plug-in life cycle
 */
public class HALEUIPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.ui"; //$NON-NLS-1$

	// The shared instance
	private static HALEUIPlugin plugin;

	private final ReportService repService = PlatformUI.getWorkbench()
			.getService(ReportService.class);

	/**
	 * Default constructor
	 */
	public HALEUIPlugin() {
		super();
	}

	/**
	 * @see AbstractUIPlugin#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		// start plugin
		super.start(context);
		plugin = this;

		// reload reports
		this.repService.loadReportsOnStartup();
	}

	/**
	 * @see AbstractUIPlugin#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// save reports
		this.repService.saveReportsOnShutdown();

		// remove temporary databases
		OrientInstanceService ois = OrientInstanceService.getExistingInstance();
		if (ois != null) {
			ois.dispose();
		}

		// shutdown plugin
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static HALEUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

}
