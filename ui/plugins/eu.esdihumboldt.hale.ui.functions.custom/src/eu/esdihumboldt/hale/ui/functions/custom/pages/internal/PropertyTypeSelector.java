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

package eu.esdihumboldt.hale.ui.functions.custom.pages.internal;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Preconditions;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.function.common.PropertyEntityDialog;
import eu.esdihumboldt.hale.ui.util.selector.AbstractSelector;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Selector for type definitions.
 * 
 * @author Simon Templer
 */
public class PropertyTypeSelector extends AbstractSelector<TypeDefinition, EntityDefinition> {

	private final String dialogTitle;
	private final SchemaSpaceID ssid;

	/**
	 * Create a type definition selector.
	 * 
	 * @param parent the parent composite
	 * @param dialogTitle the title for the selection dialog
	 * @param ssid the schema space identifier
	 * @param filters the view filters or <code>null</code>
	 */
	public PropertyTypeSelector(Composite parent, String dialogTitle, SchemaSpaceID ssid,
			ViewerFilter[] filters) {
		super(parent, new DefinitionLabelProvider(), filters);

		Preconditions.checkNotNull(ssid);

		this.dialogTitle = dialogTitle;
		this.ssid = ssid;
	}

	@Override
	protected TypeDefinition convertFrom(EntityDefinition object) {
		DefinitionGroup defGroup = DefinitionUtil.getDefinitionGroup(object.getDefinition());
		if (defGroup instanceof TypeDefinition) {
			return (TypeDefinition) defGroup;
		}
		return null;
	}

	/**
	 * @see AbstractSelector#createSelectionDialog(Shell)
	 */
	@Override
	protected AbstractViewerSelectionDialog<EntityDefinition, ?> createSelectionDialog(
			Shell parentShell) {
		return new PropertyEntityDialog(parentShell, ssid, null, dialogTitle, null);
	}
}
