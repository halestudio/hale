/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.common.definition.selector;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.util.selector.AbstractSelector;
import eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog;

/**
 * Selector for type definitions.
 * 
 * @author Simon Templer
 */
public class TypeDefinitionSelector extends AbstractSelector<TypeDefinition> {

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
		super(parent, new DefinitionLabelProvider(), filters);
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
