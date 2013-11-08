package eu.esdihumboldt.hale.ui.util.groovy.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class GroovyUIPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.ui.util.groovy"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static GroovyUIPlugin plugin;

	/**
	 * The constructor
	 */
	public GroovyUIPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

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
	public static GroovyUIPlugin getDefault() {
		return plugin;
	}

}
