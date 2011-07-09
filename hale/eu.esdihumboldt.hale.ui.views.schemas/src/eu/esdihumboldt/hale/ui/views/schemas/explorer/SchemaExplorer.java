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

import eu.esdihumboldt.hale.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;

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
		tree = new TreeViewer(main, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER); //XXX for now not lazy XXX | SWT.VIRTUAL);
		tree.setUseHashlookup(true);
		tree.setLabelProvider(new SchemaExplorerLabelProvider());
		tree.setContentProvider(new TypeIndexContentProvider(tree));
		tree.getControl().setLayoutData(GridDataFactory.fillDefaults().
				grab(true, true).create());
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
	
//	/**
//	 * A helper method for setting up the two SchemaExplorers.
//	 * 
//	 * @param modelComposite
//	 *            the parent {@link Composite} to use.
//	 * @param schemaType the viewer type
//	 * @return a {@link TreeViewer} with the currently loaded schema.
//	 */
//	private TreeViewer schemaExplorerSetup(Composite modelComposite, final SchemaSpaceID schemaType) {
//		PatternFilter patternFilter = new PatternFilter();
//	    final FilteredTree filteredTree = new FilteredTree(modelComposite, SWT.MULTI
//	            | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
//	    TreeViewer schemaViewer = filteredTree.getViewer();
//	    // set the default content provider, settings must match initial action state (be careful: [asIs, invert, invert])
//		schemaViewer.setContentProvider(new ConfigurableModelContentProvider(false, false, true));
//		ModelNavigationViewLabelProvider labelProvider = new ModelNavigationViewLabelProvider();
//		schemaViewer.setLabelProvider(labelProvider);
//		schemaViewer.setInput(schemaItemService.getRoot(schemaType));
//       schemaViewer
//				.addSelectionChangedListener(new ISelectionChangedListener() {
//					@Override
//					public void selectionChanged(SelectionChangedEvent event) {
//						updateSelection();
//					}
//				});
//       
//       // add tool tip
//		new ColumnBrowserTip(schemaViewer, 400, 300, true, 0, labelProvider);
//       
//		return schemaViewer;
//	}
	
}
