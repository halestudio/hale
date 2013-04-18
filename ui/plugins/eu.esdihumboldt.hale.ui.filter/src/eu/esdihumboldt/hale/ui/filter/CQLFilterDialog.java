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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;

/**
 * Dialog for configuring a CQL type filter.
 * 
 * @author Kai Schwierczek
 */
public class CQLFilterDialog extends TypeFilterDialog {

	private final TypeEntityDefinition type;

	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param type the type definition
	 * @param title the dialog title, <code>null</code> for a default title
	 * @param message the dialog message, <code>null</code> for a default
	 *            message
	 */
	public CQLFilterDialog(Shell parentShell, TypeEntityDefinition type, String title,
			String message) {
		super(parentShell, title, message);
		this.type = type;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.filter.TypeFilterDialog#createFilterField(Composite)
	 */
	@Override
	protected CQLFilterField createFilterField(Composite parent) {
		return new CQLFilterField(type, parent, SWT.NONE);
	}

}
