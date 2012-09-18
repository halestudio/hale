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
