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
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

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
	 * @see ViewerComparator#compare(Viewer, Object, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1 = category(e1);
        int cat2 = category(e2);

        if (cat1 != cat2) {
			return cat1 - cat2;
		}
    	
        String name1 = getSortName(e1);
        String name2 = getSortName(e2);

        // use the comparator to compare the strings
        return getComparator().compare(name1, name2);
	}

	private String getSortName(Object def) {
		if (def instanceof EntityDefinition) {
			def = ((EntityDefinition) def).getDefinition();
		}
		
		if (def instanceof Definition<?>) {
			return ((Definition<?>) def).getDisplayName();
		}
		
		return def.toString();
	}

	/**
	 * @see ViewerComparator#category(Object)
	 */
	@Override
	public int category(Object element) {
		if (element instanceof EntityDefinition) {
			element = ((EntityDefinition) element).getDefinition();
		}
		
		if (element instanceof GroupPropertyDefinition) {
			return 0;
		}
		if (element instanceof PropertyDefinition) {
			return 1;
		}
		//used if instances contain metadata, used in DefinitionInstanceTreeViewer
		if (element instanceof Set){
			return 2;
		}
		
		return super.category(element);
	}
	
}
