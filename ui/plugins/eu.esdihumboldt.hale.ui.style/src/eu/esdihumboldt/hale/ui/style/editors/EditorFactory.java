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

import org.eclipse.swt.widgets.Composite;

/**
 * Interface for an {@link Editor} factory
 * 
 * @param <T> the type that is edited with the {@link Editor}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface EditorFactory<T> {

	/**
	 * Create an editor
	 * 
	 * @param parent the parent composite
	 * @param value the initial value of the editor
	 * 
	 * @return the created editor
	 */
	public Editor<T> createEditor(Composite parent, T value);

}
