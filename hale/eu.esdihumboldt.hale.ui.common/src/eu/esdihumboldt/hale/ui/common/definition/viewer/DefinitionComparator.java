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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import java.util.Comparator;

import org.eclipse.jface.viewers.ViewerComparator;

import eu.esdihumboldt.hale.schema.model.Definition;
import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;

/**
 * Comparator for {@link Definition}s. Groups group properties and
 * normal properties.
 * @author Simon Templer
 */
public class DefinitionComparator extends ViewerComparator {

	/**
	 * Default constructor
	 */
	public DefinitionComparator() {
		super(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
	}

	/**
	 * @see ViewerComparator#category(Object)
	 */
	@Override
	public int category(Object element) {
		if (element instanceof GroupPropertyDefinition) {
			return 0;
		}
		if (element instanceof PropertyDefinition) {
			return 1;
		}
		
		return super.category(element);
	}
	
}
