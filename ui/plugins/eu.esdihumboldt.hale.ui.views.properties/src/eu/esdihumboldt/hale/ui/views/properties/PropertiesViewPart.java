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
	 * Since createPartControl does an important job this is used for subclasses.
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
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
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter.equals(IPropertySheetPage.class)) {
            return new TabbedPropertySheetPage(this);
		}
		if (adapter.equals(IContextProvider.class)) {
			return new HALEContextProvider(
					getSite().getSelectionProvider(),
					getViewContext());
		}
        return super.getAdapter(adapter);
	}

	/**
	 * Get the view's dynamic help context identifier.
	 * @return the context id or <code>null</code>
	 */
	protected String getViewContext() {
		return null;
	}

}
