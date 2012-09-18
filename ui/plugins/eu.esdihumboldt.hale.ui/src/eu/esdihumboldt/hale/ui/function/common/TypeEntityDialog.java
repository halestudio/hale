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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypesContentProvider;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Dialog for selecting a {@link TypeEntityDefinition}.
 * 
 * @author Simon Templer
 */
public class TypeEntityDialog extends EntityDialog {

	/**
	 * @see EntityDialog#EntityDialog(Shell, SchemaSpaceID, String,
	 *      EntityDefinition)
	 */
	public TypeEntityDialog(Shell parentShell, SchemaSpaceID ssid, String title,
			EntityDefinition initialSelection) {
		super(parentShell, ssid, title, initialSelection);
	}

	/**
	 * @see EntityDialog#setupViewer(TreeViewer, EntityDefinition)
	 */
	@Override
	protected void setupViewer(TreeViewer viewer, EntityDefinition initialSelection) {
		viewer.setLabelProvider(new StyledDefinitionLabelProvider());
		EntityDefinitionService entityDefinitionService = (EntityDefinitionService) PlatformUI
				.getWorkbench().getService(EntityDefinitionService.class);
		viewer.setContentProvider(new TreePathProviderAdapter(new EntityTypesContentProvider(
				viewer, entityDefinitionService, ssid)));

		SchemaService ss = (SchemaService) PlatformUI.getWorkbench()
				.getService(SchemaService.class);

		viewer.setInput(ss.getSchemas(ssid));

		if (initialSelection instanceof TypeEntityDefinition) {
			viewer.setSelection(new StructuredSelection(initialSelection.getType()));
		}
	}

	/**
	 * @see EntityDialog#getObjectFromSelection(ISelection)
	 */
	@Override
	protected EntityDefinition getObjectFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof TypeEntityDefinition) {
				return (EntityDefinition) element;
			}
			if (element instanceof TypeDefinition) {
				return new TypeEntityDefinition((TypeDefinition) element, ssid, null);
			}
		}

		return null;
	}

	/**
	 * @see EntityDialog#getObject()
	 */
	@Override
	public TypeEntityDefinition getObject() {
		return (TypeEntityDefinition) super.getObject();
	}

}
