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

package eu.esdihumboldt.hale.ui.function.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypePropertyContentProvider;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Dialog for selecting a {@link PropertyEntityDefinition}.
 * 
 * @author Simon Templer
 */
public class PropertyEntityDialog extends EntityDialog {

	private final TypeEntityDefinition parentType;

	/**
	 * Create a property entity dialog
	 * 
	 * @param parentShell the parent shall
	 * @param ssid the schema space
	 * @param parentType the parent type for the property to be selected
	 * @param title the dialog title
	 * @param initialSelection the entity definition to select initially (if
	 *            possible), may be <code>null</code>
	 */
	public PropertyEntityDialog(Shell parentShell, SchemaSpaceID ssid,
			TypeEntityDefinition parentType, String title, EntityDefinition initialSelection) {
		super(parentShell, ssid, title, initialSelection);

		this.parentType = parentType;
	}

	/**
	 * @see EntityDialog#setupViewer(TreeViewer, EntityDefinition)
	 */
	@Override
	protected void setupViewer(TreeViewer viewer, EntityDefinition initialSelection) {
		viewer.setLabelProvider(new StyledDefinitionLabelProvider());
		EntityDefinitionService entityDefinitionService = (EntityDefinitionService) PlatformUI
				.getWorkbench().getService(EntityDefinitionService.class);
		viewer.setContentProvider(new TreePathProviderAdapter(
				new EntityTypePropertyContentProvider(viewer, entityDefinitionService, ssid)));

		if (parentType != null)
			viewer.setInput(parentType);
		else {
			SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(
					SchemaService.class);
			viewer.setInput(ss.getSchemas(ssid));
		}

		if (initialSelection != null) {
			viewer.setSelection(new StructuredSelection(initialSelection));
		}
	}

	/**
	 * @see EntityDialog#getObjectFromSelection(ISelection)
	 */
	@Override
	protected EntityDefinition getObjectFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof EntityDefinition) {
				return (EntityDefinition) element;
			}
		}

		if (!selection.isEmpty() && selection instanceof ITreeSelection) {
			// create property definition w/ default contexts
			TreePath path = ((ITreeSelection) selection).getPaths()[0];

			// get parent type
			TypeDefinition type = ((PropertyDefinition) path.getFirstSegment()).getParentType();
			// determine definition path
			List<ChildContext> defPath = new ArrayList<ChildContext>();
			for (int i = 0; i < path.getSegmentCount(); i++) {
				defPath.add(new ChildContext((ChildDefinition<?>) path.getSegment(i)));
			}
			// TODO check if property entity definition is applicable?
			return new PropertyEntityDefinition(type, defPath, ssid, parentType.getFilter());
		}

		return null;
	}

}
