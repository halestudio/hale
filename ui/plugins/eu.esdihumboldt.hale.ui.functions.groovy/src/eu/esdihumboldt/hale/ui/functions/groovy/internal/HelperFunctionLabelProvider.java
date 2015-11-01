/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.cst.functions.groovy.helper.Category;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunction;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.impl.HelperFunctionSpecification;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.CommonSharedImagesConstants;

/**
 * Label provider for helper function tray
 * 
 * @author sameer sheikh
 */
public class HelperFunctionLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

	/**
	 * Gets the label image for a Category or a functions
	 * 
	 * @param element A category or a function object
	 * @return label image for a category or a function
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof Category) {
			return CommonSharedImages.getImageRegistry().get(
					CommonSharedImagesConstants.IMG_DEFINITION_GROUP);
		}
		else if (element instanceof HelperFunctionOrCategory) {
			return CommonSharedImages.getImageRegistry().get(
					CommonSharedImagesConstants.IMG_FUNCTION);
		}
		return null;
	}

	/**
	 * Gets the text associated for a category or a function
	 * 
	 * @param element A category or a function
	 * @return text for the given category or function.
	 */
	@Override
	public String getText(Object element) {

		if (element instanceof Category) {
			return ((Category) element).getName();
		}
		else if (element instanceof HelperFunctionOrCategory) {
			return ((HelperFunctionOrCategory) element).getName();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
	 */
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		String elementName = null;

		if (element instanceof Category) {
			cell.setText(((Category) element).getName());
			cell.setImage(CommonSharedImages.getImageRegistry().get(
					CommonSharedImagesConstants.IMG_DEFINITION_GROUP));
		}
		else if (element instanceof HelperFunctionOrCategory) {

			HelperFunctionSpecification hfs = null;
			elementName = ((HelperFunctionOrCategory) element).getName();
			StyledString text = new StyledString(elementName);
			try {
				HelperFunction<?> helper = ((HelperFunctionOrCategory) element).asFunction();
				hfs = (HelperFunctionSpecification) helper.getSpec(elementName);
				text.append(PageFunctions.getStyledParameters(hfs));
			} catch (Exception e) {
				//
			}

			cell.setText(text.getString());
			cell.setImage(CommonSharedImages.getImageRegistry().get(
					CommonSharedImagesConstants.IMG_FUNCTION));
			cell.setStyleRanges(text.getStyleRanges());

		}

		super.update(cell);
	}
}
