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

package eu.esdihumboldt.hale.ui.common.function.viewer;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.extension.category.Category;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;

/**
 * Function label provider
 * 
 * @author Simon Templer
 */
public class FunctionLabelProvider extends LabelProvider {

	private final Map<String, Image> urlImages = new HashMap<String, Image>();

	/**
	 * @see LabelProvider#getText(Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Category) {
			return ((Category) element).getName();
		}
		if (element instanceof AbstractFunction) {
			return ((AbstractFunction<?>) element).getDisplayName();
		}

		return super.getText(element);
	}

	/**
	 * @see LabelProvider#getImage(Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Category) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}

		// get image based on getIconURL in AbstractFunction (and cache them)
		if (element instanceof AbstractFunction) {
			URL iconUrl = ((AbstractFunction<?>) element).getIconURL();
			String iconString = iconUrl.toString();

			Image image = urlImages.get(iconString);

			if (image == null) {
				try {
					image = ImageDescriptor.createFromURL(iconUrl).createImage();
					if (image != null) {
						urlImages.put(iconString, image);
					}
				} catch (Throwable e) {
					// ignore
				}
			}

			return image;
		}

		return super.getImage(element);
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// dispose any images created
		for (Image image : urlImages.values()) {
			image.dispose();
		}
		urlImages.clear();

		super.dispose();
	}

}
