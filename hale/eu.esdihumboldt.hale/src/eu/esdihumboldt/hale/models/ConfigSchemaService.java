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

package eu.esdihumboldt.hale.models;

import java.util.List;

import eu.esdihumboldt.hale.models.project.generated.ConfigSchema;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ConfigSchemaService extends UpdateService {

	/**
	 * Add a key => value pair to the config.
	 * 
	 * @param key
	 * @param value
	 */
	public void add(String key, String value);
	
	/**
	 * Remove an entry at specific key.
	 * 
	 * @param key
	 */
	public void remove(String key);
	
	/**
	 * Get a value from specific key.
	 * 
	 * @param key
	 * @return value
	 */
	public String get(String key);
	
	/**
	 * Retuns a {@link List} with all {@link ConfigSchema} items.
	 * 
	 * @return
	 */
	public List<ConfigSchema> getAll();
	
	/**
	 * Sets all {@link ConfigSchema} items.
	 * 
	 * @param list
	 */
	public void setAll(List<ConfigSchema> list);
}
