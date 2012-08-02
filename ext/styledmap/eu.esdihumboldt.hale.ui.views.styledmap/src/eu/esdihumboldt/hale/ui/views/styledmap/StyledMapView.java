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

package eu.esdihumboldt.hale.ui.views.styledmap;

import org.eclipse.help.IContextProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.WorkbenchPart;

import de.fhg.igd.mapviewer.view.MapView;
import eu.esdihumboldt.hale.ui.HALEContextProvider;
import eu.esdihumboldt.hale.ui.HaleUI;

/**
 * Extends map view with some functionality from PropertiesViewPart.
 * @author Simon Templer
 */
public class StyledMapView extends MapView {
	
	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.styledmap";

	/**
	 * @see MapView#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		HaleUI.registerWorkbenchUndoRedo(getViewSite());
		super.createPartControl(parent);
	}

	/**
	 * @see WorkbenchPart#getAdapter(Class)
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
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
		return "eu.esdihumboldt.hale.doc.user.views.styledmap.view";
	}
	
}
