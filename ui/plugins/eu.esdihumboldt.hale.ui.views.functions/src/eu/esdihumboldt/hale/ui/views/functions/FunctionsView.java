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
