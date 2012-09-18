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

package eu.esdihumboldt.hale.ui.selection.impl;

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;

import eu.esdihumboldt.hale.ui.selection.InstanceSelection;

/**
 * Default instance selection implementation.
 * 
 * @author Simon Templer
 */
public class DefaultInstanceSelection extends StructuredSelection implements InstanceSelection {

	/**
	 * Creates an empty selection
	 */
	public DefaultInstanceSelection() {
		super();
	}

	/**
	 * @see StructuredSelection#StructuredSelection(List)
	 */
	public DefaultInstanceSelection(@SuppressWarnings("rawtypes") List elements) {
		super(elements);
	}

	/**
	 * @see StructuredSelection#StructuredSelection(Object)
	 */
	public DefaultInstanceSelection(Object element) {
		super(element);
	}

	/**
	 * @see StructuredSelection#StructuredSelection(Object[])
	 */
	public DefaultInstanceSelection(Object[] elements) {
		super(elements);
	}

}
