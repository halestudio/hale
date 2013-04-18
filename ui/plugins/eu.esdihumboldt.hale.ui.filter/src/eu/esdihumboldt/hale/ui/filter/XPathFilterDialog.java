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

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Dialog for configuring a XPath entity filter.
 * 
 * @author Kai Schwierczek
 */
public class XPathFilterDialog extends TypeFilterDialog {

	private final EntityDefinition entity;

	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param entity the entity
	 * @param title the dialog title, <code>null</code> for a default title
	 * @param message the dialog message, <code>null</code> for a default
	 *            message
	 */
	public XPathFilterDialog(Shell parentShell, EntityDefinition entity, String title,
			String message) {
		super(parentShell, title, message);
		this.entity = entity;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.filter.TypeFilterDialog#createFilterField(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected TypeFilterField createFilterField(Composite parent) {
		return new XPathFilterField(entity, parent, SWT.NONE);
	}

}
