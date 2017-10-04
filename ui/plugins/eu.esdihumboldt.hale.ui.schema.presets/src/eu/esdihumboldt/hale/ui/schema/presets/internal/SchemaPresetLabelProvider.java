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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaCategory;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaCategoryExtension;
import eu.esdihumboldt.hale.common.schema.presets.extension.SchemaPreset;

/**
 * Label provider for {@link SchemaPreset}s.
 * 
 * @author Simon Templer
 */
public class SchemaPresetLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

	private final Image defImage = AbstractUIPlugin.imageDescriptorFromPlugin(
			"eu.esdihumboldt.hale.ui.schema.presets", "icons/def_preset.gif").createImage();
	private final Image categoryImage = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJ_FOLDER);

	private final Map<String, Image> urlImages = new HashMap<String, Image>();

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();

		StyledString text = new StyledString();

		if (element instanceof SchemaPreset) {
			SchemaPreset schema = (SchemaPreset) element;
			text.append(schema.getName());
		}
		if (element instanceof SchemaCategory) {
			text.append(((SchemaCategory) element).getName());
		}

		if (element instanceof SchemaPreset) {
			SchemaPreset preset = (SchemaPreset) element;

			String version = preset.getVersion();
			if (version != null) {
				text.append(" " + version, StyledString.COUNTER_STYLER);
			}

			String tag = preset.getTag();
			if (tag != null) {
				text.append(" (" + tag + ")", StyledString.DECORATIONS_STYLER);
			}
		}

		// If element was not handled yet, delegate to getText (e.g. for
		// NoObject.NONE)
		if (element != null && (text.getString() == null || text.getString().isEmpty())) {
			text.append(getText(element));
		}

		cell.setImage(getImage(element));
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());

		super.update(cell);
	}

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

			return defImage;
		}
		if (element instanceof SchemaCategory) {
			return categoryImage;
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof SchemaPreset) {
			SchemaPreset schema = (SchemaPreset) element;
			// name and category name for search
			return schema.getName() + "("
					+ SchemaCategoryExtension.getInstance().get(schema.getCategoryId()).getName()
					+ ")";
		}
		if (element instanceof SchemaCategory) {
			return ((SchemaCategory) element).getName();
		}

		return element.toString();
	}

	@Override
	public void dispose() {
		for (Image image : urlImages.values()) {
			image.dispose();
		}
		urlImages.clear();

		defImage.dispose();

		super.dispose();
	}

}
