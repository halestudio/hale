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
			imageKey = ISharedImages.IMG_TOOL_FORWARD;
		}
		else if (to.getType().equals(TreeObjectType.CONCRETE_FT)) {
			imageKey = ISharedImages.IMG_TOOL_UP;
		}
		else if (to.getType().equals(TreeObjectType.SIMPLE_ATTRIBUTE)) {
			imageKey = ISharedImages.IMG_OBJ_FILE;
		} 
		else if (to.getType().equals(TreeObjectType.COMPLEX_ATTRIBUTE)) {
			imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		}
		else if (to.getType().equals(TreeObjectType.GEOMETRIC_ATTRIBUTE)) {
			imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		}
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
