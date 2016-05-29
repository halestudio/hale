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

package eu.esdihumboldt.hale.ui.function.internal;

import org.eclipse.help.IContext;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.ui.HALEContextProvider;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.function.contribution.SchemaSelectionFunctionMatcher;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;
import eu.esdihumboldt.hale.ui.util.wizard.ViewerWizardSelectionPage;
import eu.esdihumboldt.util.Pair;

/**
 * Page for creating a new relation
 * 
 * @author Simon Templer
 */
public class NewRelationPage extends ViewerWizardSelectionPage {

	/**
	 * Configuration parameter name for the last selected function
	 */
	protected static final String CONF_LAST_SELECTED_FUNCTION = "eu.esdihumboldt.hale.ui.function.new_relation.last_function";

	private TreeViewer viewer;

	private final SchemaSelection initialSelection;

	private final SchemaSelectionFunctionMatcher selectionMatcher;

	private HALEContextProvider contextProvider;

	private IPageChangedListener changeListener;

	/**
	 * @param title the page title
	 * @param initialSelection the initial selection to initialize the wizard
	 *            with, may be <code>null</code> to start with an empty
	 *            configuration
	 * @param selectionMatcher the matcher that determines if a function is
	 *            applicable for the initial selection, may be <code>null</code>
	 *            to allow all functions
	 */
	public NewRelationPage(String title, SchemaSelection initialSelection,
			SchemaSelectionFunctionMatcher selectionMatcher) {
		super("newRelation");

		this.initialSelection = initialSelection;
		this.selectionMatcher = selectionMatcher;

		setTitle(title);
	}

	/**
	 * @see ViewerWizardSelectionPage#createViewer(Composite)
	 */
	@Override
	protected Pair<StructuredViewer, Control> createViewer(Composite parent) {
		PatternFilter filter = new PatternFilter();
		filter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent,
				SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);

		viewer = tree.getViewer();
		viewer.setContentProvider(new FunctionWizardNodeContentProvider(getContainer(),
				initialSelection, selectionMatcher));
		viewer.setLabelProvider(new FunctionWizardNodeLabelProvider());
		// no input needed, but we have to set something
		viewer.setInput(Boolean.TRUE);

		// set focus on viewer control to prevent odd behavior
		viewer.getControl().setFocus();

		// expand selection
		viewer.expandAll();

		// selection context
		contextProvider = new HALEContextProvider(viewer, null);

		// help update on page shown
		if (getContainer() instanceof IPageChangeProvider) {
			((IPageChangeProvider) getContainer())
					.addPageChangedListener(changeListener = new IPageChangedListener() {

						@Override
						public void pageChanged(PageChangedEvent event) {
							if (event.getSelectedPage() == NewRelationPage.this) {
								// update the help button
								if (getContainer() instanceof HaleWizardDialog) {
									((HaleWizardDialog) getContainer())
											.setHelpButtonEnabled(getHelpContext() != null);
								}
							}
						}

					});
		}

		// help update on selection change
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IContext context = getHelpContext();

				// update the help button
				if (getContainer() instanceof HaleWizardDialog) {
					((HaleWizardDialog) getContainer()).setHelpButtonEnabled(context != null);
				}

				// update the help
				if (context != null) {
					TrayDialog trayDialog = (TrayDialog) getContainer();
					if (trayDialog.getTray() != null) {
						// if the tray is already open, update the help
						performHelp();
					}
				}
			}
		});

		// load page configuration
		// XXX would be better if called from outside
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		restore(ps.getConfigurationService());

		return new Pair<StructuredViewer, Control>(viewer, tree);
	}

	@Override
	public void performHelp() {
		IContext helpContext = getHelpContext();

		if (helpContext == null) {
			TrayDialog trayDialog = (TrayDialog) getContainer();
			if (trayDialog.getTray() != null) {
				trayDialog.closeTray();
			}
		}
		else {
			PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpContext);
		}
	}

	private IContext getHelpContext() {
		if (contextProvider != null) {
			ISelection sel = viewer.getSelection();
			if (!sel.isEmpty() && sel instanceof IStructuredSelection) {
				return contextProvider.getContext(((IStructuredSelection) sel).getFirstElement());
			}
		}

		return null;
	}

	@Override
	protected int getViewerHeightHint() {
		return 300;
	}

	/**
	 * Get the current function wizard
	 * 
	 * @return the function wizard or <code>null</code>
	 */
	public FunctionWizard getFunctionWizard() {
		IWizardNode node = getSelectedNode();
		if (node instanceof FunctionWizardNode) {
			return ((FunctionWizardNode) node).getWizard();
		}

		return null;
	}

	/**
	 * Store the configuration to a configuration service
	 * 
	 * @param configurationService the configuration service
	 */
	public void store(IConfigurationService configurationService) {
		if (viewer != null) {
			// store selected function ID
			ISelection selection = viewer.getSelection();

			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				Object selectedObject = ((IStructuredSelection) selection).getFirstElement();
				if (selectedObject instanceof FunctionWizardNode) {
					String functionId = ((FunctionWizardNode) selectedObject).getFunction().getId();
					configurationService.set(CONF_LAST_SELECTED_FUNCTION, functionId);
					return;
				}
			}
		}

		configurationService.set(CONF_LAST_SELECTED_FUNCTION, null);
	}

	/**
	 * Load the configuration from a configuration service
	 * 
	 * @param configurationService the configuration service
	 */
	public void restore(IConfigurationService configurationService) {
		if (viewer != null) {
			String functionId = configurationService.get(CONF_LAST_SELECTED_FUNCTION);

			if (functionId != null) {
				// create function wizard node and select it
				FunctionDefinition<?> function = FunctionUtil.getFunction(functionId,
						HaleUI.getServiceProvider());
				if (function != null) {
					FunctionWizardNode node = new FunctionWizardNode(function, getContainer(),
							initialSelection);
					viewer.setSelection(new StructuredSelection(node), true);
				}
			}
		}
	}

	@Override
	public void dispose() {
		if (changeListener != null) {
			IWizardContainer container = getContainer();
			if (container instanceof IPageChangeProvider) {
				((IPageChangeProvider) container).removePageChangedListener(changeListener);
			}
		}

		super.dispose();
	}

}
