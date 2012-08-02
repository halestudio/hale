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
	 * Name for the SourceID value stored in instance meta data
	 */
	public static final String METADATA_SOURCEID = "SourceID";
	
	
	/**
	 * sets the ID of an instance in its meta data
	 * @param instance the certain instance to change
	 * @param id the id to set
	 */
	public static void setID(MutableInstance instance, Object id){
		instance.setMetaData(METADATA_ID, id);
	}
	
	/**
	 * sets the SourceID of an instance in its meta data
	 * @param instance the certain instance to change
	 * @param id the id to set, can be multiple IDs
	 */
	public static void setSourceID(MutableInstance instance, Object... id){
		instance.setMetaData(METADATA_SOURCEID, id);
	}
	
	/**
	 * gets the SourceID of an instance from its metadata
	 * @param instance the instance to get the SourceID from
	 * @return he first SourceID of the instance, my be <code> null <code> 
	 * if the instance doesn't contain an id in its meta data
	 */
	public static String getSourceID(Instance instance){
	List<Object> data = instance.getMetaData(METADATA_SOURCEID);
		
		if(data.isEmpty()){
			return null;
		}
		
		else {
			return data.get(0).toString();
		}
	}
	
	/**
	 * gets the ID of an instance from its metadata
	 * @param instance the instance with the certain id
	 * @return the first id of the instance, my be <code> null <code> 
	 * if the instance doesn't contain an id in its meta data
	 */
	public static String getID(Instance instance){
		List<Object> data = instance.getMetaData(METADATA_ID);
		
		if(data.isEmpty()){
			return null;
		}
		
		else {
			return data.get(0).toString();
		}
	}
}
