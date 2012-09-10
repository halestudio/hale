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
