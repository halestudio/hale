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

/**
 * Dialog for selecting a {@link TypeEntityDefinition}.
 * @author Simon Templer
 */
public class TypeEntityDialog extends EntityDialog {

	/**
	 * @see EntityDialog#EntityDialog(Shell, SchemaSpaceID, String, EntityDefinition) 
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
		EntityDefinitionService entityDefinitionService = (EntityDefinitionService) PlatformUI.getWorkbench().getService(EntityDefinitionService.class);
		viewer.setContentProvider(new EntityTypesContentProvider(
				viewer, entityDefinitionService, ssid));
		
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		viewer.setInput(ss.getSchemas(ssid));
		
		if (initialSelection instanceof TypeEntityDefinition) {
			viewer.setSelection(new StructuredSelection(
					initialSelection.getType()));
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
