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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypePropertyContentProvider;
import eu.esdihumboldt.hale.ui.util.selector.AbstractTreeSelectionDialog;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Dialog for selecting a {@link PropertyDefinition} with its complete property
 * path (represented in an {@link EntityDefinition}).
 * @author Simon Templer
 */
public class PropertyDefinitionDialog extends AbstractTreeSelectionDialog<EntityDefinition> {
	
	private final TypeDefinition parentType;
	
	private final SchemaSpaceID ssid;

	/**
	 * Create a property entity dialog 
	 * @param parentShell the parent shall
	 * @param ssid the schema space used for creating {@link PropertyEntityDefinition},
	 *   may be <code>null</code> if not needed
	 * @param parentType the parent type for the property to be selected
	 * @param title the dialog title
	 * @param initialSelection the entity definition to select initially (if
	 *   possible), may be <code>null</code>
	 */
	public PropertyDefinitionDialog(Shell parentShell, SchemaSpaceID ssid,
			TypeDefinition parentType, String title, EntityDefinition initialSelection) {
		super(parentShell, title, initialSelection);
		
		this.ssid = ssid;
		this.parentType = parentType;
	}
	
	/**
	 * @see AbstractTreeSelectionDialog#createViewer(Composite)
	 */
	@Override
	protected TreeViewer createViewer(Composite parent) {
		// create viewer
		SchemaPatternFilter patternFilter = new SchemaPatternFilter() {
			@Override
			protected boolean matches(Viewer viewer, Object element) {
				boolean superMatches = super.matches(viewer, element);
				if (!superMatches)
					return false;
				return acceptObject(viewer, getFilters(), ((TreePath)element).getLastSegment());
			}
		};
		patternFilter.setUseEarlyReturnIfMatcherIsNull(false);
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new TreePathFilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
		tree.getViewer().setComparator(new DefinitionComparator());
		return tree.getViewer();
	}

	@Override
	protected void setupViewer(TreeViewer viewer, EntityDefinition initialSelection) {
		viewer.setLabelProvider(new DefinitionLabelProvider());
		viewer.setContentProvider(new TreePathProviderAdapter(
				new TypePropertyContentProvider(viewer)));
		
		viewer.setInput(parentType);
		
		if (initialSelection != null) {
			viewer.setSelection(new StructuredSelection(initialSelection));
		}
	}

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
			//TODO check if property entity definition is applicable? 
			return new PropertyEntityDefinition(type, defPath, ssid, null);
		}
		
		return null;
	}

}
