/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy.internal;

import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.google.common.collect.ImmutableList;

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants;
import eu.esdihumboldt.cst.functions.groovy.helper.Category;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.Argument;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.util.IColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovyColorManager;
import eu.esdihumboldt.hale.ui.util.groovy.GroovySourceViewerUtil;
import eu.esdihumboldt.hale.ui.util.groovy.SimpleGroovySourceViewerConfiguration;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathProviderAdapter;

/**
 * Tools for helper function dialog page
 * 
 * @author Sameer Sheikh
 */
public class PageFunctions extends DialogTray implements GroovyConstants {

	/**
	 * Creates a tool item for a helper function dialg page
	 * 
	 * @param toolbar the tool bar
	 * @param page the dialog page
	 */
	public static void createToolItem(ToolBar toolbar, final HaleWizardPage<?> page) {
		ToolItem item = new ToolItem(toolbar, SWT.PUSH);
		item.setToolTipText("Show functions");
		item.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_FUNCTION));
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (page.getContainer() instanceof TrayDialog) {
					TrayDialog tray = (TrayDialog) page.getContainer();
					if (tray.getTray() != null) {
						tray.closeTray();
					}
					tray.openTray(new PageFunctions());
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// do nothing

			}
		});

	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogTray#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(comp);
		Label label = new Label(comp, SWT.NONE);
		label.setText("Functions Overview");
		label.setFont(JFaceResources.getHeaderFont());

		// tree viwever
		PatternFilter patternFilter = new TreePathPatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		final FilteredTree filteredTree = new TreePathFilteredTree(comp, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER, patternFilter, true);

		TreeViewer tree = filteredTree.getViewer();
		tree.setUseHashlookup(true);
		HelperFunctionLabelProvider labelProvider = new HelperFunctionLabelProvider();
		tree.setLabelProvider(labelProvider);
		IContentProvider contentProvider;

		HelperFunctionsService functions = HaleUI.getServiceProvider().getService(
				HelperFunctionsService.class);

		contentProvider = new TreePathProviderAdapter(new HelperFunctionContentProvider(functions));

		tree.setContentProvider(contentProvider);
		GridDataFactory.fillDefaults().grab(true, true).hint(280, 400).applyTo(filteredTree);

		tree.setComparator(new DefinitionComparator());

		tree.setInput(Category.ROOT);

		// Examples
		Label example = new Label(comp, SWT.NONE);
		example.setText("Example: functions build");
		// source viewer
		final SourceViewer viewer = new SourceViewer(comp, null, SWT.MULTI | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);

		final IColorManager colorManager = new GroovyColorManager();
		SourceViewerConfiguration configuration = new SimpleGroovySourceViewerConfiguration(
				colorManager, ImmutableList.of(BINDING_TARGET, BINDING_BUILDER, BINDING_INDEX,
						BINDING_SOURCE, BINDING_SOURCE_TYPES, BINDING_TARGET_TYPE, BINDING_CELL,
						BINDING_LOG, BINDING_CELL_CONTEXT, BINDING_FUNCTION_CONTEXT,
						BINDING_TRANSFORMATION_CONTEXT), null);
		viewer.configure(configuration);

		GridDataFactory.fillDefaults().grab(true, false).hint(200, 130)
				.applyTo(viewer.getControl());

		// make sure the color manager is disposed
		viewer.getControl().addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				colorManager.dispose();
			}
		});

		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IDocument doc = new Document();
				GroovySourceViewerUtil.setupDocument(doc);
				String eg = null;
				if (!event.getSelection().isEmpty()) {
					TreeSelection treesel = (TreeSelection) event.getSelection();
					TreePath[] paths = treesel.getPaths();
					if (paths != null) {
						TreePath path = paths[0];
						for (int i = 0; i < path.getSegmentCount(); i++) {
							if (path.getSegment(i) instanceof Category) {
								eg = "// Select a function to see an example.";
							}
							else if (path.getSegment(i) instanceof HelperFunctionOrCategory) {
								try {
									eg = getFunctionSpec((HelperFunctionOrCategory) path
											.getSegment(i));
								} catch (Exception e) {
									eg = "There is a problem in retrieving specification for a function.";
								}

							}
						}
					}
				}
				doc.set(eg);

				viewer.setDocument(doc);
			}

			/**
			 * get the specification of a given function
			 * 
			 * @param f a function or a category
			 * @return a string representation of a function specification.
			 * @throws Exception if it fails to get a specification
			 */
			private String getFunctionSpec(HelperFunctionOrCategory f) throws Exception {

				StringBuilder example = new StringBuilder();
				Specification spec = f.asFunction().getSpec(f.getName());

				example.append("Description: \n");
				example.append("\t");
				example.append(spec.getDescription());
				example.append(" \n\nParameters: \n");
				for (Argument arg : spec.getArguments()) {
					example.append("\t");
					example.append(arg.getName());
					example.append(" : ");
					example.append(arg.getDescription());
				}
				example.append("\n\nReturns: \n");
				example.append("\t");
				example.append(spec.getResultDescription());

				return example.toString();
			}
		});
		return comp;

	}
}
