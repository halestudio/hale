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

package eu.esdihumboldt.hale.common.instance.model;

import java.util.List;

/**
 * Utility class for signing IDs to instance metadatas
 * @author Sebastian Reinhardt
 */
public class InstanceMetadata {

	
	/**
	 * Name for the ID value stored in instance meta data
	 */
	public static final String METADATA_ID = "ID";
	
	
	/**
	 * sets the ID of an instance in its meta data
	 * @param instance the certain instance to change
	 * @param id the id to set
	 */
	public static void setID(MutableInstance instance, String id){
		instance.setMetaData(METADATA_ID, id);
	}
	
	/**
	 * gets the ID of an instance from its metadata
	 * @param instance the instance with the certain id
	 * @return the id of the instance, my be <code> null <code> 
	 * if the instance doesn't contain an id in its meta data
	 */
	public static String getID(MutableInstance instance){
		List<Object> data = instance.getMetaData(METADATA_ID);
		
		if(data.isEmpty()){
			return null;
		}
		
		else {
			return data.get(0).toString();
		}
	}
}
