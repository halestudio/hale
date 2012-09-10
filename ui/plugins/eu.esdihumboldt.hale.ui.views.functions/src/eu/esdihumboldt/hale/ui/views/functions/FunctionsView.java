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

package eu.esdihumboldt.hale.ui.views.functions;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionContentProvider;
import eu.esdihumboldt.hale.ui.common.function.viewer.FunctionLabelProvider;
import eu.esdihumboldt.hale.ui.util.viewer.ViewerMenu;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;

/**
 * Functions view
 * 
 * @author Simon Templer
 */
public class FunctionsView extends PropertiesViewPart {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.functions";

	private TreeViewer viewer;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setLabelProvider(new FunctionLabelProvider());
		viewer.setContentProvider(new FunctionContentProvider());

		// no input needed, but we have to set something
		viewer.setInput(Boolean.TRUE);

		new ViewerMenu(getSite(), viewer);

		getSite().setSelectionProvider(viewer);
	}

	/**
	 * @see PropertiesViewPart#getViewContext()
	 */
	@Override
	protected String getViewContext() {
		return "eu.esdihumboldt.hale.doc.user.functions";
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
