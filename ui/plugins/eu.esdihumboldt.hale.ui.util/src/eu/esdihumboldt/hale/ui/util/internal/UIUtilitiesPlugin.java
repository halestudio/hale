package eu.esdihumboldt.hale.ui.util.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Simon Templer
 */
public class UIUtilitiesPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.ui.util"; //$NON-NLS-1$

	// The shared instance
	private static UIUtilitiesPlugin plugin;

	/**
	 * The constructor
	 */
	public UIUtilitiesPlugin() {
		// do nothing
	}

	/**
	 * @see AbstractUIPlugin#start(BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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
	public static UIUtilitiesPlugin getDefault() {
		return plugin;
	}

}
