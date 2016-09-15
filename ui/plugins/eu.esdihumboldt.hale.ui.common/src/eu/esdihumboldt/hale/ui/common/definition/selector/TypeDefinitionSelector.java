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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.definition.selector;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.util.selector.AbstractSelector;
import eu.esdihumboldt.hale.ui.util.selector.AbstractUniformSelector;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Selector for type definitions.
 * 
 * @author Simon Templer
 */
public class TypeDefinitionSelector extends AbstractUniformSelector<TypeDefinition> {

	private final String dialogTitle;
	private final TypeIndex typeIndex;

	/**
	 * Create a type definition selector.
	 * 
	 * @param parent the parent composite
	 * @param dialogTitle the title for the selection dialog
	 * @param typeIndex the types to choose from
	 * @param filters the view filters or <code>null</code>
	 */
	public TypeDefinitionSelector(Composite parent, String dialogTitle, TypeIndex typeIndex,
			ViewerFilter[] filters) {
		super(parent, new DefinitionLabelProvider(null), filters);
		this.dialogTitle = dialogTitle;
		this.typeIndex = typeIndex;
	}

	/**
	 * @see AbstractSelector#createSelectionDialog(Shell)
	 */
	@Override
	protected AbstractViewerSelectionDialog<TypeDefinition, ?> createSelectionDialog(
			Shell parentShell) {
		return new TypeDefinitionDialog(parentShell, dialogTitle, getSelectedObject(), typeIndex);
	}

}
