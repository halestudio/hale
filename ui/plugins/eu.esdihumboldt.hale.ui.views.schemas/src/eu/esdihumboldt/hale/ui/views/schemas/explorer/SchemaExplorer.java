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

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeIndexContentProvider;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Explorer for schema definitions
 * 
 * @author Simon Templer
 */
public class SchemaExplorer {

	private final Composite main;

	private TypeIndex schema;

	private final TreeViewer tree;

	private final SchemaSpaceID schemaSpace;

	/**
	 * Create a schema explorer
	 * 
	 * @param parent the parent composite
	 * @param title the title
	 * @param schemaSpace the associated schema space
	 */
	public SchemaExplorer(Composite parent, String title, SchemaSpaceID schemaSpace) {
		main = new Composite(parent, SWT.NONE);
		this.schemaSpace = schemaSpace;

		// set main layout
		main.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

		// create the toolbar composite
		Composite bar = new Composite(main, SWT.NONE);
		bar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		bar.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		Label titleLabel = new Label(bar, SWT.NONE);
		titleLabel.setText(title);
		titleLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

		// create tree viewer
		PatternFilter patternFilter = new SchemaPatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		final FilteredTree filteredTree = new TreePathFilteredTree(main, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER, patternFilter, true);

		tree = filteredTree.getViewer();
		tree.setUseHashlookup(true);
		SchemaExplorerLabelProvider labelProvider = new SchemaExplorerLabelProvider(tree);
		tree.setLabelProvider(labelProvider);
		tree.setContentProvider(createContentProvider(tree));
		tree.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		ClassificationFilter classFilter = new ClassificationFilter(tree);
		tree.addFilter(classFilter);

		tree.setComparator(new DefinitionComparator());

		// create the toolbar
		Control toolbar = createToolbar(bar, classFilter);
		toolbar.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

		// TODO add delay for tooltip
//		new ColumnBrowserTip(tree, 400, 300, true, 0, labelProvider);
	}

	/**
	 * Refresh the tree viewer in the display thread.
	 */
	protected void refreshInDisplayThread() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				tree.refresh();
			}
		});
	}

	/**
	 * @return the associated schema space
	 */
	public SchemaSpaceID getSchemaSpace() {
		return schemaSpace;
	}

	/**
	 * Create the content provider
	 * 
	 * @param tree the tree viewer
	 * @return the content provider
	 */
	protected IContentProvider createContentProvider(TreeViewer tree) {
		return new TreePathProviderAdapter(new TypeIndexContentProvider(tree));
	}

	/**
	 * Create the tool-bar
	 * 
	 * @param parent the parent composite
	 * @param classFilter the classification filter
	 * @return the main control of the toolbar
	 */
	protected Control createToolbar(Composite parent, ClassificationFilter classFilter) {
		ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP);

		ToolBarManager manager = new ToolBarManager(toolBar);

		prependToolbarActions(manager);

		// disabled rarely used filters not to clutter up the UI
//		manager.add(new ClassificationFilterAction(Classification.ABSTRACT_FT,
//				"Hide abstract feature types", "Show abstract feature types",
//				"/icons/see_abstract_ft.png", classFilter));
//
//		manager.add(new ClassificationFilterAction(Classification.CONCRETE_FT,
//				"Hide concrete feature types", "Show concrete feature types",
//				"/icons/see_concrete_ft.png", classFilter));
//
//		manager.add(new ClassificationFilterAction(Classification.ABSTRACT_TYPE,
//				"Hide abstract property types", "Show abstract property types",
//				"/icons/see_abstract_type.png", classFilter));
//
//		manager.add(new ClassificationFilterAction(Classification.CONCRETE_TYPE,
//				Messages.ModelNavigationView_PropertyHide,
//				Messages.ModelNavigationView_PropertyShow, "/icons/see_concrete_type.png",
//				classFilter));
//
//		manager.add(new Separator());
//
//		manager.add(new ClassificationFilterAction(Classification.STRING_PROPERTY,
//				Messages.ModelNavigationView_StringHide, Messages.ModelNavigationView_StringShow,
//				"/icons/see_string_attribute.png", classFilter));
//
//		manager.add(new ClassificationFilterAction(Classification.NUMERIC_PROPERTY,
//				Messages.ModelNavigationView_NumericHide, Messages.ModelNavigationView_NumericShow,
//				"/icons/see_number_attribute.png", classFilter));
//
//		manager.add(new ClassificationFilterAction(Classification.GEOMETRIC_PROPERTY,
//				Messages.ModelNavigationView_GeometryHide,
//				Messages.ModelNavigationView_GeometryShow, "/icons/see_geometry_attribute.png",
//				classFilter));
//
//		manager.add(new ClassificationFilterAction(Classification.COMPLEX_PROPERTY,
//				"Hide complex properties", "Show complex properties",
//				"/icons/see_property_type.gif", classFilter));

		manager.update(false);

		return toolBar;
	}

	/**
	 * Prepend toolbar actions to the schema explorer tool bar.
	 * 
	 * @param manager the toolbar manager
	 */
	protected void prependToolbarActions(ToolBarManager manager) {
		// none to add
	}

	/**
	 * Get the schema
	 * 
	 * @return the schema
	 */
	public TypeIndex getSchema() {
		return schema;
	}

	/**
	 * Get the internal tree viewer of the view
	 * 
	 * @return the tree viewer
	 */
	public TreeViewer getTreeViewer() {
		return tree;
	}

	/**
	 * Set the schema
	 * 
	 * @param schema the schema to set
	 */
	public void setSchema(TypeIndex schema) {
		this.schema = schema;
		tree.setInput(schema);
		refreshInDisplayThread();
	}

	/**
	 * Get the schema explorer main control, e.g. to apply a layout.
	 * 
	 * @return the main control
	 */
	public Control getControl() {
		return main;
	}

}
