/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.ui.filter;

import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.ui.filter.FilterDialogFactory;

/**
 * Factory for {@link XPathFilterDialog}.
 * 
 * @author Simon Templer
 */
public class XPathFilterDialogFactory implements FilterDialogFactory {

	@Override
	public Filter openDialog(Shell shell, EntityDefinition entityDef, String title, String message) {
		XPathFilterDialog dialog = new XPathFilterDialog(shell, entityDef, title, message);
		if (dialog.open() == XPathFilterDialog.OK) {
			return dialog.getFilter();
		}
		return null;
	}

}
