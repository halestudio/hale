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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypePropertyContentProvider;
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
		viewer.setContentProvider(new TypePropertyContentProvider(viewer));
		
		viewer.setInput(parentType);

		//TODO filter??
	}

	/**
	 * @see EntityDialog#getEntityFromSelection(ISelection)
	 */
	@Override
	protected EntityDefinition getEntityFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof ITreeSelection) {
			TreePath path = ((ITreeSelection) selection).getPaths()[0];
			
			List<Definition<?>> defPath = new ArrayList<Definition<?>>();
			// add parent type
			defPath.add(((PropertyDefinition) path.getFirstSegment()).getParentType());
			// add properties
			for (int i = 0; i < path.getSegmentCount(); i++) {
				defPath.add((Definition<?>) path.getSegment(i));
			}
			return new PropertyEntityDefinition(defPath);
		}
		
		return null;
	}

}
