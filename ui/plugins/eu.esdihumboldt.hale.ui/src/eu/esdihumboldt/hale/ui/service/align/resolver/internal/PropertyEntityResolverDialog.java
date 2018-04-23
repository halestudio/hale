/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.service.align.resolver.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.function.common.PropertyEntityDialog;

/**
 * Entity resolver dialog.
 * 
 * @author Simon Templer
 */
public class PropertyEntityResolverDialog extends PropertyEntityDialog {

	/**
	 * Skip button ID.
	 */
	public static final int SKIP = IDialogConstants.CLIENT_ID + 2;

	/**
	 * @see PropertyEntityDialog#PropertyEntityDialog(Shell, SchemaSpaceID,
	 *      TypeEntityDefinition, String, EntityDefinition)
	 */
	public PropertyEntityResolverDialog(Shell parentShell, SchemaSpaceID ssid,
			TypeEntityDefinition parentType, String title, EntityDefinition initialSelection) {
		super(parentShell, ssid, parentType, title, initialSelection);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		createButton(parent, SKIP, "Skip", false);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case SKIP:
			internalSetSelected(null);
			setReturnCode(SKIP);
			close();
			break;
		default:
			super.buttonPressed(buttonId);
		}
	}

}
