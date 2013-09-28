/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.filter;

import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;

/**
 * Creates a dialog for adding or editing a filter/condition on an entity
 * definition.
 * 
 * @author Simon Templer
 */
public interface FilterDialogFactory {

	/**
	 * Open the dialog and return the filter.
	 * 
	 * @param shell the parent shell
	 * @param entityDef the entity definition, if the entity definition has a
	 *            filter/condition attached, it should be edited
	 * @param title the dialog title
	 * @param message the dialog message
	 * @return the filter or <code>null</code> if the user cancelled the dialog
	 */
	public Filter openDialog(Shell shell, EntityDefinition entityDef, String title, String message);

}
