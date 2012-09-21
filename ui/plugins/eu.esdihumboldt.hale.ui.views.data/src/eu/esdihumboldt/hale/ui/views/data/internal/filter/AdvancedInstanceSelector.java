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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.data.internal.filter;

import org.eclipse.swt.widgets.Button;

/**
 * Instance selector that can adapt the associated radio button in the data
 * view, e.g. changing the image.
 * 
 * @author Simon Templer
 */
public interface AdvancedInstanceSelector extends InstanceSelector {

	/**
	 * Sets the button that activates the selector.
	 * 
	 * @param activator the activator button
	 */
	public void setActivator(Button activator);

}
