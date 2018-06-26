/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.common.viewers;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

/**
 * Label provider that draws an image and centers it within the table cell.
 * 
 * @author Florian Esser
 */
public abstract class CenteredImageCellLabelProvider extends OwnerDrawLabelProvider {

	@Override
	protected void measure(Event event, Object element) {
		// Don't measure
	}

	@Override
	protected void paint(Event event, Object element) {
		Image image = getImage(element);
		if (image != null) {
			Rectangle itemBounds = ((TableItem) event.item).getBounds(event.index);
			Rectangle imageBounds = image.getBounds();

			itemBounds.width /= 2;
			itemBounds.width -= imageBounds.width / 2;
			itemBounds.height /= 2;
			itemBounds.height -= imageBounds.height / 2;

			int x = itemBounds.width > 0 ? itemBounds.x + itemBounds.width : itemBounds.x;
			int y = itemBounds.height > 0 ? itemBounds.y + itemBounds.height : itemBounds.y;

			event.gc.drawImage(image, x, y);
		}
	}

	/**
	 * Provide the image for the given element
	 * 
	 * @param element the element
	 * @return the image to draw
	 */
	public abstract Image getImage(Object element);
}
