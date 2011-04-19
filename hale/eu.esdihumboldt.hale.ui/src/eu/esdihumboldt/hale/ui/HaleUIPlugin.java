/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator that controls the life cycle of the bundle and provides
 * functionality for Eclipse UI plug-ins.
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class HaleUIPlugin extends AbstractUIPlugin implements HaleSharedImages {
	
	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.ui"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static HaleUIPlugin _plugin;
	
	/**
	 * The constructor
	 */
	public HaleUIPlugin() {
		//nothing to do here
	}

	/**
	 * @see AbstractUIPlugin#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;
	}

	/**
	 * @see AbstractUIPlugin#stop(BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	/**
	 * @return the shared instance
	 */
	public static HaleUIPlugin getDefault() {
		return _plugin;
	}

	/**
	 * @see AbstractUIPlugin#initializeImageRegistry(ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		
		reg.put(IMG_EXPORT_WIZARD, imageDescriptorFromPlugin(PLUGIN_ID, "/icons/export_wiz.gif"));
	}
	
}
