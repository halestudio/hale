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

package eu.esdihumboldt.hale.ui.codelist.inspire.internal;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.common.inspire.codelists.CodeListRef;

/**
 * Label provider for {@link CodeListRef}s.
 * 
 * @author Simon Templer
 */
public class CodeListLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

	private final Image defImage = AbstractUIPlugin.imageDescriptorFromPlugin(
			"eu.esdihumboldt.hale.ui.codelist.inspire", "icons/inspire.gif").createImage();
	private final Image categoryImage = PlatformUI.getWorkbench().getSharedImages()
			.getImage(ISharedImages.IMG_OBJ_FOLDER);

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();

		StyledString text;

		if (element instanceof CodeListRef) {
			CodeListRef cl = (CodeListRef) element;

			text = new StyledString(cl.getName());

			String schema = cl.getSchemaName();
			if (schema != null) {
				text.append(" (" + schema + ")", StyledString.COUNTER_STYLER);
			}
//
//			String tag = cl.getTag();
//			if (tag != null) {
//				text.append(" (" + tag + ")", StyledString.DECORATIONS_STYLER);
//			}
		}
		else {
			text = new StyledString(getText(element));
		}

		cell.setImage(getImage(element));
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());

		super.update(cell);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof CodeListRef) {
			return defImage;
		}

		if (element instanceof String) {
			return categoryImage;
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof CodeListRef) {
			CodeListRef cl = (CodeListRef) element;
			// code list, schema and theme names for search
			return cl.getName() + " " + cl.getSchemaName() + " " + cl.getThemeName();
		}

		return element.toString();
	}

	@Override
	public void dispose() {
		defImage.dispose();

		super.dispose();
	}

}
