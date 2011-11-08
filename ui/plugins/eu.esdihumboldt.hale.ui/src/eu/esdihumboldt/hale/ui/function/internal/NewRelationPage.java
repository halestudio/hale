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

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import eu.esdihumboldt.hale.ui.function.FunctionWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ViewerWizardSelectionPage;
import eu.esdihumboldt.util.Pair;

/**
 * Page for creating a new relation
 * @author Simon Templer
 */
public class NewRelationPage extends ViewerWizardSelectionPage {

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
		FilteredTree tree = new FilteredTree(parent, SWT.SINGLE | SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL, filter , true);
		
		TreeViewer viewer = tree.getViewer();
		viewer.setContentProvider(new FunctionWizardNodeContentProvider(getContainer()));
		viewer.setLabelProvider(new FunctionWizardNodeLabelProvider());
		// no input needed, but we have to set something
		viewer.setInput(Boolean.TRUE);
		
		//TODO set viewer filter?!
		
		return new Pair<StructuredViewer, Control>(viewer, tree);
	}

	/**
	 * Get the current function wizard
	 * @return the function wizard or <code>null</code>
	 */
	public FunctionWizard getFunctionWizard() {
		IWizardNode node = getSelectedNode();
		if (node instanceof FunctionWizardNode) {
			return ((FunctionWizardNode) node).getWizard();
		}
		
		return null;
	}
	
}
