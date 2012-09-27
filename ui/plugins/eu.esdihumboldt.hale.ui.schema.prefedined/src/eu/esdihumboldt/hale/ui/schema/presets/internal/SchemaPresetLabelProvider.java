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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.schema.presets.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.ui.schema.presets.extension.SchemaPreset;

/**
 * Label provider for {@link SchemaPreset}s.
 * 
 * @author Simon Templer
 */
public class SchemaPresetLabelProvider extends LabelProvider {

	private final Map<String, Image> urlImages = new HashMap<String, Image>();

	@Override
	public Image getImage(Object element) {
		if (element instanceof SchemaPreset) {
			SchemaPreset schema = (SchemaPreset) element;
			URL iconURL = schema.getIconURL();
			if (iconURL != null) {
				String key = iconURL.toString();
				Image image = urlImages.get(key);
				if (image == null) {
					image = ImageDescriptor.createFromURL(iconURL).createImage();
					urlImages.put(key, image);
				}
				return image;
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof SchemaPreset) {
			SchemaPreset schema = (SchemaPreset) element;
			return schema.getName();
		}
		return super.getText(element);
	}

	@Override
	public void dispose() {
		for (Image image : urlImages.values()) {
			image.dispose();
		}
		urlImages.clear();

		super.dispose();
	}

}
