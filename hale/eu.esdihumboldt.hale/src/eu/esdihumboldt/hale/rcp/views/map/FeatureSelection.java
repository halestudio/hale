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

package eu.esdihumboldt.hale.rcp.views.map;

import java.util.Set;

import org.eclipse.jface.viewers.StructuredSelection;
import org.opengis.filter.identity.FeatureId;

/**
 * Feature selection
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureSelection extends StructuredSelection {

	/**
	 * Create an empty selection
	 */
	public FeatureSelection() {
		super();
	}

	/**
	 * Create a selection with the given feature ids
	 * 
	 * @param ids
	 */
	public FeatureSelection(Set<FeatureId> ids) {
		super(ids.toArray());
	}

	/**
	 * Create a selection with the given feature id
	 * 
	 * @param id
	 */
	public FeatureSelection(FeatureId id) {
		super(id);
	}

}
