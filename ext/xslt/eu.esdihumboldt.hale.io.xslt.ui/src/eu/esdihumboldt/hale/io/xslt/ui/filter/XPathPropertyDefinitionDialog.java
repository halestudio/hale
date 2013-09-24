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

package eu.esdihumboldt.hale.io.xslt.ui.filter;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.selector.PropertyDefinitionDialog;

/**
 * Enhanced {@link PropertyDefinitionDialog} for some XPath features.
 * 
 * @author Kai Schwierczek
 */
public class XPathPropertyDefinitionDialog extends PropertyDefinitionDialog {

	private static final QName PARENT_NAME = new QName("dummy_ns", "..");

	private int parentCount = 0;
	private EntityDefinition currentEntity;

	/**
	 * Create a dialog.
	 * 
	 * @param parentShell the parent shall
	 * @param entityDef the entity definition to use as starting point
	 * @param title the dialog title
	 * @param initialSelection the entity definition to select initially (if
	 *            possible), may be <code>null</code>
	 */
	public XPathPropertyDefinitionDialog(Shell parentShell, EntityDefinition entityDef,
			String title, EntityDefinition initialSelection) {
		super(parentShell, entityDef.getSchemaSpace(), getInputDefinition(entityDef), title,
				initialSelection);
		currentEntity = entityDef;
//		setFilters(new ViewerFilter[] { new ViewerFilter() {
//
//			@Override
//			public boolean select(Viewer viewer, Object parentElement, Object element) {
//				if (element instanceof Definition<?>)
//					return !((Definition<?>) element).getName().equals(PARENT_NAME);
//				return true;
//			}
//		} });
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.selector.PropertyDefinitionDialog#setupViewer(org.eclipse.jface.viewers.TreeViewer,
	 *      eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	protected void setupViewer(TreeViewer viewer, EntityDefinition initialSelection) {
		super.setupViewer(viewer, initialSelection);
	}

	private void parentSelected() {
		List<ChildContext> path;
		do {
			currentEntity = AlignmentUtil.getParent(currentEntity);
			path = currentEntity.getPropertyPath();
			// skip groups
		} while (!path.isEmpty() && path.get(path.size() - 1).getChild().asProperty() == null);
		parentCount++;
		getViewer().setInput(getInputDefinition(currentEntity));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.selector.AbstractViewerSelectionDialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		Definition<?> def = getDefinitionFromSelection(getViewer().getSelection());
		if (def != null && def.getName().equals(PARENT_NAME))
			parentSelected();
		else
			super.okPressed();
	}

	/**
	 * Returns how many steps the user went up in the hierarchy.
	 * 
	 * @return how many steps the user went up in the hierarchy
	 */
	public int getParentCount() {
		return parentCount;
	}

	/**
	 * Returns whether the current selection is from a top level type.
	 * 
	 * @return whether the current selection is from a top level type
	 */
	public boolean atTopLevel() {
		return currentEntity.getPropertyPath() == null || currentEntity.getPropertyPath().isEmpty();
	}

	private static TypeDefinition getInputDefinition(EntityDefinition entityDef) {
		if (entityDef.getPropertyPath() == null || entityDef.getPropertyPath().isEmpty()) {
			// for type entity simply return the type
			// maybe also add feature to go above this and select from all
			// types?
			return entityDef.getType();
		}
		else {
			PropertyDefinition def = (PropertyDefinition) entityDef.getDefinition();

			// create a dummy type for the input
			TypeDefinition dummyType = new DefaultTypeDefinition(new QName("ValueFilterDummy"));
			TypeDefinition emptyType = new DefaultTypeDefinition(new QName("EmptyDummy"));

			// with .. as parent link
			new DefaultPropertyDefinition(PARENT_NAME, dummyType, emptyType);
			// and the value property added as itself
			dummyType.addChild(def);

			return dummyType;
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.selector.PropertyDefinitionDialog#getObjectFromSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	protected EntityDefinition getObjectFromSelection(ISelection selection) {
		Definition<?> def = getDefinitionFromSelection(selection);
		if (def != null && def.getName().equals(PARENT_NAME))
			return null;

		return super.getObjectFromSelection(selection);
	}

	/**
	 * Returns the selected definition, if the selection isn't empty and its
	 * first element is a definition.
	 * 
	 * @param selection the selection
	 * @return the selected definition
	 */
	private Definition<?> getDefinitionFromSelection(ISelection selection) {
		if (selection.isEmpty())
			return null;
		if (selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection).getFirstElement();
			if (selected instanceof Definition<?>)
				return (Definition<?>) selected;
		}
		return null;
	}
}
