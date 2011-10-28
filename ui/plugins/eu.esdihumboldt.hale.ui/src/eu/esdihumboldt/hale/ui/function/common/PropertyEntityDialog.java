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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypePropertyContentProvider;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.hale.ui.service.entity.util.EntityTypePropertyContentProvider;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Dialog for selecting a {@link PropertyEntityDefinition}
 * @author Simon Templer
 */
public class PropertyEntityDialog extends EntityDialog {
	
	private final TypeDefinition parentType;

	/**
	 * Create a property entity dialog 
	 * @param parentShell the parent shall
	 * @param ssid the schema space
	 * @param parentType the parent type for the property to be selected
	 * @param title the dialog title
	 */
	public PropertyEntityDialog(Shell parentShell, SchemaSpaceID ssid,
			TypeDefinition parentType, String title) {
		super(parentShell, ssid, title);
		
		this.parentType = parentType;
	}

	/**
	 * @see EntityDialog#setupViewer(TreeViewer)
	 */
	@Override
	protected void setupViewer(TreeViewer viewer) {
		viewer.setLabelProvider(new DefinitionLabelProvider());
		//XXX entity content provider for both source and target?
		switch (ssid) {
		case SOURCE:
			viewer.setContentProvider(new TypePropertyContentProvider(viewer));
			break;
		case TARGET:
		default:
			EntityDefinitionService entityDefinitionService = (EntityDefinitionService) PlatformUI.getWorkbench().getService(EntityDefinitionService.class);
			viewer.setContentProvider(new EntityTypePropertyContentProvider(
					viewer, entityDefinitionService));
		}
		
		viewer.setInput(parentType);

		//TODO filter??
	}

	/**
	 * @see EntityDialog#getEntityFromSelection(ISelection)
	 */
	@Override
	protected EntityDefinition getEntityFromSelection(ISelection selection) {
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
			//TODO check if property entity definition is applicable? 
			return new PropertyEntityDefinition(type, defPath);
		}
		
		return null;
	}

}
