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

package eu.esdihumboldt.hale.ui.cst.contribution;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import de.cs3d.ui.util.eclipse.extension.AbstractExtensionContribution;
import de.cs3d.ui.util.eclipse.extension.selective.SelectiveExtensionContribution;
import de.cs3d.util.eclipse.extension.selective.SelectiveExtension;
import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHookFactory;
import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHooks;

/**
 * Contribution for transformation tree hooks.
 * 
 * @author Simon Templer
 */
public class TransformationTreeHooksContribution extends
		SelectiveExtensionContribution<TransformationTreeHook, TransformationTreeHookFactory> {

	/**
	 * @see AbstractExtensionContribution#initExtension()
	 */
	@Override
	protected SelectiveExtension<TransformationTreeHook, TransformationTreeHookFactory> initExtension() {
		return OsgiUtils.getService(TransformationTreeHooks.class);
	}

	/**
	 * @see AbstractExtensionContribution#fillWithFactories(Menu, List, int)
	 */
	@Override
	protected int fillWithFactories(Menu parent, List<TransformationTreeHookFactory> factories,
			int index) {
		if (factories.isEmpty()) {
			// add a dummy item
			MenuItem item = new MenuItem(parent, SWT.PUSH, index++);
			item.setEnabled(false);
			item.setText("None available");
		}
		return super.fillWithFactories(parent, factories, index);
	}

}
