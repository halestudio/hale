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

package eu.esdihumboldt.hale.ui.views.properties;

import org.eclipse.help.IContextProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.ui.HALEContextProvider;
import eu.esdihumboldt.hale.ui.HaleUI;

/**
 * View part that provides support for association with the properties view
 * based on the property contributor that has support for {@link Definition}s.
 * 
 * @author Simon Templer
 */
public abstract class PropertiesViewPart extends ViewPart
		implements ITabbedPropertySheetPageContributor {

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public final void createPartControl(Composite parent) {
		HaleUI.registerWorkbenchUndoRedo(getViewSite());
		createViewControl(parent);
	}

	/**
	 * Since createPartControl does an important job this is used for
	 * subclasses.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * @param parent the parent control
	 */
	protected abstract void createViewControl(Composite parent);

	/**
	 * @see ITabbedPropertySheetPageContributor#getContributorId()
	 */
	@Override
	public String getContributorId() {
		return "eu.esdihumboldt.hale.ui.views.properties";
	}

	/**
	 * @see WorkbenchPart#getAdapter(Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.equals(IPropertySheetPage.class)) {
			return new TabbedPropertySheetPage(this);
		}
		if (adapter.equals(IContextProvider.class)) {
			return new HALEContextProvider(getSite().getSelectionProvider(), getViewContext());
		}
		return super.getAdapter(adapter);
	}

	/**
	 * Get the view's dynamic help context identifier.
	 * 
	 * @return the context id or <code>null</code>
	 */
	protected String getViewContext() {
		return null;
	}

}
