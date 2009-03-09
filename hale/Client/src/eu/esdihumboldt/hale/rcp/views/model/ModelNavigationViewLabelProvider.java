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
package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.rcp.Application;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * Provides Images for the elements of the data models in the 
 * ModelNavigationView.
 * 
 * @author Thorsten Reitz, Fraunhofer IGD
 * @version $Id$
 */
public class ModelNavigationViewLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	
	/**
	 * Returns an adjusted image depending on the type of the object passed in.
	 * @return an Image
	 */
	@Override
	public Image getImage(Object obj) {
		TreeObject to = (TreeObject) obj;
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		
		if (to.getType().equals(TreeObjectType.ROOT)) {
			imageKey = ISharedImages.IMG_DEF_VIEW;
		}
		else if (to.getType().equals(TreeObjectType.ABSTRACT_FT)) {
			imageKey = "abstract_ft.png";
		}
		else if (to.getType().equals(TreeObjectType.CONCRETE_FT)) {
			imageKey = "concrete_ft.png";
		}
		else if (to.getType().equals(TreeObjectType.SIMPLE_ATTRIBUTE)) {
			imageKey = "string_attribute.png";
		} 
		else if (to.getType().equals(TreeObjectType.COMPLEX_ATTRIBUTE)) {
			imageKey = "number_attribute.png";
		}
		else if (to.getType().equals(TreeObjectType.GEOMETRIC_ATTRIBUTE)) {
			imageKey = "geometry_attribute.png";
		}
		return AbstractUIPlugin.imageDescriptorFromPlugin(
			HALEActivator.PLUGIN_ID, "/icons/" + imageKey).createImage();

	}
}
