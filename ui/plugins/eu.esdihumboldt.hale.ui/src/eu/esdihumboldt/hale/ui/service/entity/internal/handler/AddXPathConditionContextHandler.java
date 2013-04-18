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

package eu.esdihumboldt.hale.ui.service.entity.internal.handler;

import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.filter.TypeFilterDialog;
import eu.esdihumboldt.hale.ui.filter.XPathFilterDialog;

/**
 * Adds a new XPath condition context for a selected {@link EntityDefinition}.
 * 
 * @author Kai Schwierczek
 */
public class AddXPathConditionContextHandler extends AbstractAddConditionContextHandler {

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.internal.handler.AbstractAddConditionContextHandler#createDialog(org.eclipse.swt.widgets.Shell,
	 *      eu.esdihumboldt.hale.common.align.model.EntityDefinition,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	protected TypeFilterDialog createDialog(Shell shell, EntityDefinition entityDef, String title,
			String message) {
		return new XPathFilterDialog(shell, entityDef, title, message);
	}

}
