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

package eu.esdihumboldt.hale.ui.views.data.internal.filter;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance selection listener interface
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceSelectionListener {

	/**
	 * Called when the selection changed
	 * 
	 * @param type the definition
	 * @param selection the selected features
	 */
	public void selectionChanged(TypeDefinition type, Iterable<Instance> selection);

	/**
	 * Called before {@link #selectionChanged(TypeDefinition, Iterable)}
	 */
	public void preSelectionChange();

	/**
	 * Called after {@link #selectionChanged(TypeDefinition, Iterable)}
	 */
	public void postSelectionChange();

}
