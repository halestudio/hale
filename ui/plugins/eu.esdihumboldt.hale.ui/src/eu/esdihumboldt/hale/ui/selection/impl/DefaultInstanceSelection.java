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
