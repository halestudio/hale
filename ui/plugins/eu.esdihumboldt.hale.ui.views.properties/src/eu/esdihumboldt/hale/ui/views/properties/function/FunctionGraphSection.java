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

package eu.esdihumboldt.hale.ui.views.properties.function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.zest.core.viewers.GraphViewer;
import eu.esdihumboldt.hale.common.align.extension.function.Function;
import eu.esdihumboldt.hale.ui.common.graph.content.FunctionGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.FunctionGraphLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.layout.FunctionTreeLayoutAlgorithm;

/**
 * Function section with source and target information modeled in a graph
 * 
 * @author Patrick Lieb
 * @param <F>
 *            the function for the section
 */
public class FunctionGraphSection<F extends Function> extends
		DefaultFunctionSection<F> {

	private GraphViewer viewer;

	private FunctionTreeLayoutAlgorithm treeAlgorithm;

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite compparent = getWidgetFactory().createComposite(parent);
		compparent.setLayout(new FormLayout());

		Composite composite = getWidgetFactory().createComposite(compparent);
		composite.setLayout(new FillLayout());
		FormData data = new FormData();
		data.width = 100;
		data.height = 150;
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, -0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);

		viewer = new GraphViewer(composite, SWT.NONE);
		treeAlgorithm = new FunctionTreeLayoutAlgorithm();
		composite.setLayoutData(data);
		viewer.setLayoutAlgorithm(treeAlgorithm, true);
		viewer.setContentProvider(new FunctionGraphContentProvider());
		viewer.setLabelProvider(new FunctionGraphLabelProvider(true));

	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	@Override
	public void refresh() {
		viewer.setInput(getFunction());
		viewer.refresh();
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#shouldUseExtraSpace()
	 */
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

}
