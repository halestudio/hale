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

package eu.esdihumboldt.hale.ui.service.instance;

import eu.esdihumboldt.hale.ui.service.instance.InstanceService.DatasetType;
import eu.esdihumboldt.hale.ui.service.instance.crs.CRSDefinition;


/**
 * Listener for instance services
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface InstanceServiceListener {
	
	/**
	 * Called when a data set has changed
	 * 
	 * @param type the data set type
	 */
	public void datasetChanged(DatasetType type);
	
	/**
	 * Called when the coordinate reference system has been changed
	 * 
	 * @param crs the new CRS definition 
	 */
	public void crsChanged(CRSDefinition crs);

}
