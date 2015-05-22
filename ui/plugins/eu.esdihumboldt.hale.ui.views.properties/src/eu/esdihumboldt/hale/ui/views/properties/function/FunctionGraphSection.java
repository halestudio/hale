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

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.graph.content.FunctionGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.FunctionGraphLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.layout.FunctionTreeLayoutAlgorithm;

/**
 * Function section with source and target information modeled in a graph
 * 
 * @author Patrick Lieb
 * @param <F> the function for the section
 */
public class FunctionGraphSection<F extends FunctionDefinition<?>> extends
		DefaultFunctionSection<F> {

	private GraphViewer viewer;

	private final FunctionTreeLayoutAlgorithm treeAlgorithm = new FunctionTreeLayoutAlgorithm();

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		Composite compparent = getWidgetFactory().createComposite(parent);
		compparent.setLayout(new FormLayout());

		Composite composite = getWidgetFactory().createComposite(compparent);
		composite.setLayout(new FillLayout());
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, -0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, -ITabbedPropertyConstants.VSPACE);
		composite.setLayoutData(data);

		viewer = new GraphViewer(composite, SWT.NONE);
		viewer.setLayoutAlgorithm(treeAlgorithm, true);
		viewer.setContentProvider(new FunctionGraphContentProvider());
		viewer.setLabelProvider(new FunctionGraphLabelProvider(HaleUI.getServiceProvider(), true));
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
