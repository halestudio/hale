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

package eu.esdihumboldt.hale.ui.views.report.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.ui.views.report.properties.tree.ReportTreeContentProvider;
import eu.esdihumboldt.hale.ui.views.report.properties.tree.ReportTreeLabelProvider;

/**
 * Default details page for {@link Report}s.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ReportDetails extends AbstractReportDetails {
	
	/**
	 * The FilteredTree
	 */
	public FilteredTree tree;
	
	/**
	 * @see AbstractPropertySection#createControls(Composite, TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		PatternFilter filter = new PatternFilter();
		tree = new FilteredTree(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL, filter, true);
		
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		tree.setLayoutData(data);

		TreeViewer viewer = tree.getViewer();
		viewer.setContentProvider(new ReportTreeContentProvider());
		viewer.setLabelProvider(new ReportTreeLabelProvider());
	}
	
	/**
	 * @see AbstractPropertySection#setInput(IWorkbenchPart, ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		Object report = null;
		if (selection instanceof IStructuredSelection) {
			// overwrite element with first element from selection
			report = ((IStructuredSelection) selection).getFirstElement();
		}
		
		// set new report
		if (report instanceof Report) {
			this.report = (Report<?>) report;
		}
		
		// provide input for tree
		tree.getViewer().setInput(report);
	}
}
