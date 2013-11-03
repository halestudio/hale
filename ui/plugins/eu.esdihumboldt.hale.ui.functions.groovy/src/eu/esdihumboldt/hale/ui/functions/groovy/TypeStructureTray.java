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

package eu.esdihumboldt.hale.ui.functions.groovy;

import java.util.Collection;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.common.definition.viewer.StyledDefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeDefinitionContentProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypePropertyContentProvider;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Dialog tray displaying a type structure.
 * 
 * @author Simon Templer
 */
public class TypeStructureTray extends DialogTray {

	/**
	 * Retrieves a list of types.
	 */
	public interface TypeProvider {

		/**
		 * @return the collection of associated types
		 */
		public Collection<? extends TypeDefinition> getTypes();

	}

	/**
	 * Create a tool item for displaying the source or target type structure in
	 * the dialog tray.
	 * 
	 * @param bar the tool bar to add the item to
	 * @param page the associated wizard page
	 * @param types the provider for the types to display
	 * @param schemaSpace the schema space
	 */
	public static void createToolItem(ToolBar bar, final HaleWizardPage<?> page,
			final SchemaSpaceID schemaSpace, final TypeProvider types) {
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		switch (schemaSpace) {
		case SOURCE:
			item.setImage(CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_SOURCE_SCHEMA));
			item.setToolTipText("Show source structure");
			break;
		case TARGET:
			item.setImage(CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_TARGET_SCHEMA));
			item.setToolTipText("Show target structure");
			break;
		}
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (page.getContainer() instanceof TrayDialog) {
					TrayDialog dialog = (TrayDialog) page.getContainer();

					// close existing tray
					if (dialog.getTray() != null) {
						dialog.closeTray();
					}

					dialog.openTray(new TypeStructureTray(types, schemaSpace));
				}
				else {
					// TODO show dialog instead?
				}
			}
		});
	}

	private final TypeProvider types;
	private final SchemaSpaceID schemaSpace;

	/**
	 * Create a type structure tray.
	 * 
	 * @param types the type provider
	 * @param schemaSpace the schema space
	 */
	public TypeStructureTray(TypeProvider types, SchemaSpaceID schemaSpace) {
		super();

		this.types = types;
		this.schemaSpace = schemaSpace;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(page);

		// retrieve the types
		Collection<? extends TypeDefinition> types = this.types.getTypes();

		// heading
		Label caption = new Label(page, SWT.NONE);
		switch (schemaSpace) {
		case SOURCE:
			caption.setText("Source structure");
			break;
		case TARGET:
			caption.setText("Target structure");
			break;
		}
		caption.setFont(JFaceResources.getHeaderFont());

		// create tree viewer
		PatternFilter patternFilter = new SchemaPatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		final FilteredTree filteredTree = new TreePathFilteredTree(page, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER, patternFilter, true);

		TreeViewer tree = filteredTree.getViewer();
		tree.setUseHashlookup(true);
		StyledDefinitionLabelProvider labelProvider = new StyledDefinitionLabelProvider();
		tree.setLabelProvider(labelProvider);
		IContentProvider contentProvider;
		if (types.size() == 1) {
			contentProvider = new TreePathProviderAdapter(new TypePropertyContentProvider(tree));
		}
		else {
			contentProvider = new TreePathProviderAdapter(new TypeDefinitionContentProvider(tree));
		}
		tree.setContentProvider(contentProvider);
		GridDataFactory.fillDefaults().grab(true, true).hint(250, SWT.DEFAULT)
				.applyTo(filteredTree);

		tree.setComparator(new DefinitionComparator());

		// set input
		if (types.size() == 1) {
			tree.setInput(types.iterator().next());
		}
		else {
			tree.setInput(types);
		}

		return page;
	}

}
