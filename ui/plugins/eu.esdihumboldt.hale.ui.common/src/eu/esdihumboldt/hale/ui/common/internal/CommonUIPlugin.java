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
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
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
		reg.put(IMG_DEFINITION_GEOMETRIC_PROPERTY,
				getImageDescriptor("icons/geometry_attribute.png")); //$NON-NLS-1$
		reg.put(IMG_DEFINITION_GROUP, getImageDescriptor("icons/package.gif"));
		reg.put(IMG_DEFINITION_CHOICE, getImageDescriptor("icons/choice.gif"));
		reg.put(IMG_DEFINITION_CONCRETE_TYPE, getImageDescriptor("icons/concrete_type.png"));
		reg.put(IMG_DEFINITION_ABSTRACT_TYPE, getImageDescriptor("icons/abstract_type.png"));
		reg.put(IMG_ADD, getImageDescriptor("icons/add.gif"));
		reg.put(IMG_REMOVE, getImageDescriptor("icons/remove.gif"));
		reg.put(IMG_DECORATION_MANDATORY, getImageDescriptor("/icons/mandatory.gif"));
		reg.put(IMG_PLAY, getImageDescriptor("icons/play.gif"));
		reg.put(IMG_STOP, getImageDescriptor("icons/stop.gif"));
		reg.put(IMG_META, getImageDescriptor("icons/meta.gif"));
		reg.put(IMG_PRIORITY_HIGH, getImageDescriptor("icons/priority_high.gif"));
		reg.put(IMG_PRIORITY_HIGHER, getImageDescriptor("icons/priority_higher.gif"));
		reg.put(IMG_PRIORITY_HIGHEST, getImageDescriptor("icons/priority_highest.gif"));
		reg.put(IMG_PRIORITY_NORMAL, getImageDescriptor("icons/priority_normal.gif"));
		reg.put(IMG_PRIORITY_LOW, getImageDescriptor("icons/priority_low.gif"));
		reg.put(IMG_PRIORITY_LOWER, getImageDescriptor("icons/priority_lower.gif"));
		reg.put(IMG_PRIORITY_LOWEST, getImageDescriptor("icons/priority_lowest.gif"));
		reg.put(IMG_REFRESH, getImageDescriptor("icons/refresh.gif"));
		reg.put(IMG_SIGNED_YES, getImageDescriptor("icons/signed_yes.gif"));
		reg.put(IMG_SIGNED_NO, getImageDescriptor("icons/signed_no.gif"));
		reg.put(IMG_TRAFFICLIGHT_GREEN, getImageDescriptor("icons/trafficlight_green.png"));
		reg.put(IMG_TRAFFICLIGHT_RED, getImageDescriptor("icons/trafficlight_red.png"));
		reg.put(IMG_TRAFFICLIGHT_YELLOW, getImageDescriptor("icons/trafficlight_yellow.png"));
		reg.put(IMG_MARKER_GREEN, getImageDescriptor("icons/marker_green.gif"));
		reg.put(IMG_MARKER_RED, getImageDescriptor("icons/marker_red.gif"));
		reg.put(IMG_MARKER_YELLOW, getImageDescriptor("icons/marker_yellow.gif"));
//		reg.put(IMG_INHERITED_ARROW, getImageDescriptor("icons/inherited_arrow.png"));
		reg.put(IMG_INHERITED_ARROW_SMALL, getImageDescriptor("icons/arrow_inh_blue.png"));
		reg.put(IMG_INHERITED_ARROW, getImageDescriptor("icons/arrow_inh_blue_wide.png"));
		reg.put(IMG_FILTER_CLEAR, getImageDescriptor("icons/filter_clear.gif"));
		reg.put(IMG_FILTER_GREY, getImageDescriptor("icons/filter_grey.gif"));
		reg.put(IMG_OPEN, getImageDescriptor("icons/open.gif"));
		reg.put(IMG_TRASH, getImageDescriptor("icons/trash.gif"));
		reg.put(IMG_SAVE, getImageDescriptor("icons/save.gif"));
		reg.put(IMG_HISTORY, getImageDescriptor("icons/history.gif"));
		reg.put(IMG_SOURCE_SCHEMA, getImageDescriptor("icons/source_types2.png"));
		reg.put(IMG_TARGET_SCHEMA, getImageDescriptor("icons/target_types2.png"));
		reg.put(IMG_SOURCE_DATA, getImageDescriptor("icons/source.png"));
		reg.put(IMG_TARGET_DATA, getImageDescriptor("icons/target.png"));
		reg.put(IMG_HELP, getImageDescriptor("icons/help.gif"));
		reg.put(IMG_FUNCTION, getImageDescriptor("icons/function.png"));
		reg.put(IMG_TASKS, getImageDescriptor("icons/tasks.gif"));
	}

}
