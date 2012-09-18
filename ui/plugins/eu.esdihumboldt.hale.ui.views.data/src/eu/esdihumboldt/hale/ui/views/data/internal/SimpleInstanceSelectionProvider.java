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

package eu.esdihumboldt.hale.ui.views.data.internal;

import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultInstanceSelection;
import eu.esdihumboldt.hale.ui.util.selection.AbstactSelectionProvider;

/**
 * Instance selection provider.
 * 
 * @author Simon Templer
 */
public class SimpleInstanceSelectionProvider extends AbstactSelectionProvider {

	/**
	 * Update the instance selection.
	 * 
	 * @param instances the selected instances
	 */
	public void updateSelection(Iterable<Instance> instances) {
		InstanceSelection selection;
		if (instances != null) {
			selection = new DefaultInstanceSelection(Iterables.toArray(instances, Instance.class));
		}
		else {
			selection = new DefaultInstanceSelection();
		}
		fireSelectionChange(selection);
	}
}
