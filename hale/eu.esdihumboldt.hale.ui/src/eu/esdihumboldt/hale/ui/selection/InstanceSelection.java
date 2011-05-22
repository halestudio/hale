/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.selection;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.StructuredSelection;
import org.opengis.filter.identity.FeatureId;

/**
 * Instance selection
 * XXX should be based on instances
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class InstanceSelection extends StructuredSelection {

	/**
	 * Create an empty selection
	 */
	public InstanceSelection() {
		super();
	}

	/**
	 * Create a selection with the given feature ids
	 * 
	 * @param ids the feature ids
	 */
	public InstanceSelection(Set<FeatureId> ids) {
		super(ids.toArray());
	}

	/**
	 * Create a selection with the given feature id
	 * 
	 * @param id the feature id
	 */
	public InstanceSelection(FeatureId id) {
		super(id);
	}

	/**
	 * Get the selected feature IDs
	 * 
	 * @return the feature IDs
	 */
	public Set<FeatureId> getFeatureIds() {
		Set<FeatureId> result = new HashSet<FeatureId>();
		for (Object id : toList()) {
			if (id instanceof FeatureId) {
				result.add((FeatureId) id);
			}
		}
		return result;
	}
	
}
