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

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;

/**
 * The {@link InstanceService} provides {@link Instance}s from different data
 * sets, e.g. the {@link DataSet#SOURCE} and {@link DataSet#TRANSFORMED} data
 * sets.
 * XXX It also triggers the transformation of the source to the target data set???
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public interface InstanceService {
	
	/**
	 * Get the instances from the given data set
	 * @param dataset the data set
	 * @return the instance collection
	 */
	public InstanceCollection getInstances(DataSet dataset);
	
	/**
	 * Add instances to the {@link DataSet#SOURCE} data set
	 * @param sourceInstances the instances to add
	 */
	public void addSourceInstances(InstanceCollection sourceInstances);
	
	/**
	 * This will remove all instances from the service.
	 */
	public void clearInstances();
	
	//FIXME what to do with the CRS? should it be part of InstanceCollection?
//	/**
//	 * Set the coordinate reference system
//	 * 
//	 * @param crs the CRS definition
//	 */
//	public void setCRS(CRSDefinition crs);
//	
//	/**
//	 * Get the coordinate reference system
//	 * 
//	 * @return the CRS definition
//	 */
//	public CRSDefinition getCRS();
	
	/**
	 * Adds an instance service listener
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(InstanceServiceListener listener);
	
	/**
	 * Removes an instance service listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(InstanceServiceListener listener);
	
}
