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
package eu.esdihumboldt.hale.ui.style.editors;

import org.eclipse.swt.widgets.Control;

/**
 * Editor interface
 * 
 * @param <T> the type to edit
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface Editor<T> {

	/**
	 * Get the value
	 * 
	 * @return the value
	 * @throws Exception if an error occurs getting the value
	 */
	public abstract T getValue() throws Exception;

	/**
	 * Set the value
	 * 
	 * @param value the value to set
	 */
	public abstract void setValue(T value);

	/**
	 * Get the editor control
	 * 
	 * @return the editor control
	 */
	public Control getControl();

	/**
	 * States if the value has been changed
	 * 
	 * @return if the value has been changed
	 */
	public boolean isChanged();

}
