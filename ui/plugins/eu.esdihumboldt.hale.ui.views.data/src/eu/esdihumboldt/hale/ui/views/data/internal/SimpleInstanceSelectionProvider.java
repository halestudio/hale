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

package eu.esdihumboldt.hale.ui.views.data.internal;

import com.google.common.collect.Iterables;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.ui.selection.InstanceSelection;
import eu.esdihumboldt.hale.ui.selection.impl.DefaultInstanceSelection;
import eu.esdihumboldt.hale.ui.util.selection.AbstactSelectionProvider;

/**
 * Instance selection provider.
 * @author Simon Templer
 */
public class SimpleInstanceSelectionProvider extends AbstactSelectionProvider {

	/**
	 * Update the instance selection.
	 * @param instances the selected instances
	 */
	public void updateSelection(Iterable<Instance> instances) {
		InstanceSelection selection;
		if (instances != null) {
			selection = new DefaultInstanceSelection(
					Iterables.toArray(instances, Instance.class));
		}
		else {
			selection = new DefaultInstanceSelection();
		}
		fireSelectionChange(selection);
	}
}
