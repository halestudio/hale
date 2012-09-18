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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * {@link Instance} selector interface.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceSelector {

	/**
	 * Add a listener. A listener that was added should be informed of the
	 * currently selected features by calling
	 * {@link InstanceSelectionListener#selectionChanged(TypeDefinition, Iterable)}
	 * 
	 * @param listener the listener to add
	 */
	public abstract void addSelectionListener(InstanceSelectionListener listener);

	/**
	 * Remove a listener
	 * 
	 * @param listener the listener to remove
	 */
	public abstract void removeSelectionListener(InstanceSelectionListener listener);

	/**
	 * Create the selector control. The control must be disposed before creating
	 * another one. When the control is disposed, the listeners will be reset
	 * 
	 * @param parent the parent composite
	 * 
	 * @return the feature selector control
	 */
	public Control createControl(Composite parent);

}
