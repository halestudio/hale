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
package eu.esdihumboldt.hale.ui.common.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import eu.esdihumboldt.hale.ui.common.CommonSharedImagesConstants;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Simon Templer
 */
public class CommonUIPlugin extends AbstractUIPlugin implements CommonSharedImagesConstants {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.ui.common"; //$NON-NLS-1$

	// The shared instance
	private static CommonUIPlugin plugin;
	
	/**
	 * Default constructor
	 */
	public CommonUIPlugin() {
		super();
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
	public static CommonUIPlugin getDefault() {
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
		
		reg.put(IMG_DEFINITION_ABSTRACT_FT, getImageDescriptor("icons/abstract_ft.png")); //$NON-NLS-1$
		reg.put(IMG_DEFINITION_CONCRETE_FT, getImageDescriptor("icons/concrete_ft.png")); //$NON-NLS-1$
		reg.put(IMG_DEFINITION_STRING_PROPERTY, getImageDescriptor("icons/string_attribute.png")); //$NON-NLS-1$
		reg.put(IMG_DEFINITION_NUMERIC_PROPERTY, getImageDescriptor("icons/number_attribute.png")); //$NON-NLS-1$
		reg.put(IMG_DEFINITION_GEOMETRIC_PROPERTY, getImageDescriptor("icons/geometry_attribute.png")); //$NON-NLS-1$
		reg.put(IMG_DEFINITION_GROUP, getImageDescriptor("icons/package.gif"));
		reg.put(IMG_DEFINITION_CHOICE, getImageDescriptor("icons/choice.gif"));
		reg.put(IMG_DEFINITION_CONCRETE_TYPE, getImageDescriptor("icons/concrete_type.png"));
		reg.put(IMG_DEFINITION_ABSTRACT_TYPE, getImageDescriptor("icons/abstract_type.png"));
		reg.put(IMG_ADD, getImageDescriptor("icons/add.gif"));
		reg.put(IMG_REMOVE, getImageDescriptor("icons/remove.gif"));
		reg.put(IMG_DECORATION_MANDATORY, getImageDescriptor("/icons/mandatory.gif"));
		reg.put(IMG_PLAY, getImageDescriptor("icons/play.gif"));
		reg.put(IMG_STOP, getImageDescriptor("icons/stop.gif"));
	}
	
}
