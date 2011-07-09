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

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TreePathProviderAdapter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.util.viewer.ColumnBrowserTip;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.tree.TreePathFilteredTree;

/**
 * Explorer for schema definitions
 * @author Simon Templer
 */
public class SchemaExplorer {
	
	private final Composite main;
	
	private TypeIndex schema;
	
	private final TreeViewer tree;

	/**
	 * Create a schema explorer
	 * @param parent the parent composite
	 */
	public SchemaExplorer(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		
		// set main layout
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
		
		//TODO filter stuff?!
		
		// create tree viewer
		PatternFilter patternFilter = new SchemaPatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		final FilteredTree filteredTree = new TreePathFilteredTree(main, SWT.MULTI
	            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
		
		tree = filteredTree.getViewer();
		tree.setUseHashlookup(true);
		SchemaExplorerLabelProvider labelProvider = new SchemaExplorerLabelProvider();
		tree.setLabelProvider(labelProvider );
		tree.setContentProvider(new TreePathProviderAdapter(
				new TypeIndexContentProvider(tree)));
		tree.getControl().setLayoutData(GridDataFactory.fillDefaults().
				grab(true, true).create());
		
		tree.setComparator(new DefinitionComparator());
		
		//TODO add delay for tooltip
		new ColumnBrowserTip(tree, 400, 300, true, 0, labelProvider);
	}
	
	/**
	 * Get the schema
	 * @return the schema
	 */
	public TypeIndex getSchema() {
		return schema;
	}
	
	/**
	 * Get the internal tree viewer of the view
	 * @return the tree viewer
	 */
	public TreeViewer getTreeViewer() {
		return tree;
	}

	/**
	 * Set the schema
	 * @param schema the schema to set
	 */
	public void setSchema(TypeIndex schema) {
		this.schema = schema;
		tree.setInput(schema);
	}

	/**
	 * Get the schema explorer main control, e.g. to apply a layout.
	 * @return the main control
	 */
	public Control getControl() {
		return main;
	}
	
}
