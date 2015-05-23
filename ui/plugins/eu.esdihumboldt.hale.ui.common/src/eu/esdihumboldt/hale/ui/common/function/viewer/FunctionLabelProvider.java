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
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;

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
		if (element instanceof FunctionDefinition<?>) {
			return ((FunctionDefinition<?>) element).getDisplayName();
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
		if (element instanceof FunctionDefinition<?>) {
			URL iconUrl = ((FunctionDefinition<?>) element).getIconURL();
			if (iconUrl != null) {
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
