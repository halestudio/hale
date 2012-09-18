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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunction;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
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

	/**
	 * @param title the page title
	 */
	protected NewRelationPage(String title) {
		super("newRelation");

		setTitle(title);
	}

	/**
	 * @see ViewerWizardSelectionPage#createViewer(Composite)
	 */
	@Override
	protected Pair<StructuredViewer, Control> createViewer(Composite parent) {
		PatternFilter filter = new PatternFilter();
		filter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new FilteredTree(parent, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL, filter, true);

		viewer = tree.getViewer();
		viewer.setContentProvider(new FunctionWizardNodeContentProvider(getContainer()));
		viewer.setLabelProvider(new FunctionWizardNodeLabelProvider());
		// no input needed, but we have to set something
		viewer.setInput(Boolean.TRUE);

		// set viewer filter
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);
		if (!AlignmentUtil.hasTypeRelation(as.getAlignment())) {
			// if there are no type relations, don't allow creating a property
			// relation
			viewer.addFilter(new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof FunctionWizardNode) {
						AbstractFunction<?> function = ((FunctionWizardNode) element).getFunction();
						if (function instanceof PropertyFunction) {
							// hide any property function nodes
							return false;
						}
					}

					return true;
				}
			});
		}

		// set focus on viewer control to prevent odd behavior
		viewer.getControl().setFocus();

		// load page configuration
		// XXX would be better if called from outside
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		restore(ps.getConfigurationService());

		return new Pair<StructuredViewer, Control>(viewer, tree);
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
				AbstractFunction<?> function = FunctionUtil.getFunction(functionId);
				if (function != null) {
					FunctionWizardNode node = new FunctionWizardNode(function, getContainer());
					viewer.setSelection(new StructuredSelection(node), true);
				}
			}
		}
	}

}
