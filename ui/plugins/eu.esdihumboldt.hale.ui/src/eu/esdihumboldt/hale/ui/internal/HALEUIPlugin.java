/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import eu.esdihumboldt.hale.ui.HaleSharedImages;
import eu.esdihumboldt.hale.ui.service.instance.internal.orient.OrientInstanceService;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * The activator class controls the plug-in life cycle
 */
public class HALEUIPlugin extends AbstractUIPlugin implements HaleSharedImages {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.ui"; //$NON-NLS-1$

	// The shared instance
	private static HALEUIPlugin plugin;
	
	private ReportService repService = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
	
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
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * @see AbstractUIPlugin#initializeImageRegistry(ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		
		reg.put(IMG_DECORATION_MANDATORY, imageDescriptorFromPlugin(PLUGIN_ID, "/icons/mandatory.gif"));
	}
}
