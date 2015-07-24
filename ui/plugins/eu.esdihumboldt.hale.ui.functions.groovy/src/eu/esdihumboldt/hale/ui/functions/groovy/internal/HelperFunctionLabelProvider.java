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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.cst.functions.groovy.helper.Category;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.CommonSharedImagesConstants;

/**
 * Label provider for helper function tray
 * 
 * @author sameer sheikh
 */
public class HelperFunctionLabelProvider extends LabelProvider {

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
}
