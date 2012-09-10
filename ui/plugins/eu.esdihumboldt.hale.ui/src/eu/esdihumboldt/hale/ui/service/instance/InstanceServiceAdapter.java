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

package eu.esdihumboldt.hale.ui.service.instance;

import eu.esdihumboldt.hale.common.instance.model.DataSet;

/**
 * Instance service listener adapter
 * 
 * @author Simon Templer
 */
public class InstanceServiceAdapter implements InstanceServiceListener {

	/**
	 * @see InstanceServiceListener#datasetChanged(DataSet)
	 */
	@Override
	public void datasetChanged(DataSet type) {
		// please override me
	}

	/**
	 * @see InstanceServiceListener#transformationToggled(boolean)
	 */
	@Override
	public void transformationToggled(boolean enabled) {
		// please override me
	}

	/**
	 * @see InstanceServiceListener#datasetAboutToChange(DataSet)
	 */
	@Override
	public void datasetAboutToChange(DataSet type) {
		// please override me
	}

}
