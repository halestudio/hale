package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;



/**
 * Provides Images for the elements of the data models in the 
 * ModelNavigationView.
 * @author cjauss
 *
 */
public class ModelNavigationViewLabelProvider extends LabelProvider{
	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	
	/**
	 * Returns a special Image for a TreeParent, or another Image for all other
	 * Objects.
	 * @return an Image
	 */
	@Override
	public Image getImage(Object obj) {
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		if (obj instanceof TreeParent)
		   imageKey = ISharedImages.IMG_OBJ_FOLDER;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
