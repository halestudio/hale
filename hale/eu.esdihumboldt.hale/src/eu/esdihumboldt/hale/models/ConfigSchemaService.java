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

import java.util.HashMap;
import java.util.List;

import eu.esdihumboldt.hale.models.config.ConfigSchemaServiceListener;
import eu.esdihumboldt.hale.models.project.generated.ConfigSection;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ConfigSchemaService {
	
	/**
	 * Adds a {@link ConfigSchemaServiceListener}.
	 * 
	 * @param sl to be notified
	 * @param section the section to listen on, <code>null</code> if you want to
	 * listen on changes for any section
	 */
	public void addListener(ConfigSchemaServiceListener sl, String section);
	
	/**
	 * Remove {@link ConfigSchemaServiceListener} from specific section.
	 * 
	 * @param section
	 * @param sl
	 */
	public void removeListener(String section, ConfigSchemaServiceListener sl);
	
	/**
	 * Remove {@link ConfigSchemaServiceListener} from all sections.
	 * 
	 * @param sl
	 */
	public void removeListener(ConfigSchemaServiceListener sl);

	/**
	 * Add a section
	 * 
	 * @param name
	 */
	public void addSection(String name);
	
	/**
	 * Add a section with data.
	 * 
	 * @param name
	 * @param data
	 */
	public void addSection(String name, HashMap<String, String> data);
	
	/**
	 * Removes a section.
	 * 
	 * @param name
	 */
	public void removeSection(String name);
	
	/**
	 * Adds an Item (or changes it).
	 * 
	 * @param section
	 * @param key
	 * @param value
	 */
	public void addItem(String section, String key, String value);
	
	/**
	 * Get an item from a section.
	 * 
	 * @param section
	 * @param key
	 * @return
	 */
	public String getItem(String section, String key);
	
	/**
	 * Returns all data from specific section.
	 * 
	 * @param name
	 * @return
	 */
	public HashMap<String, String> getSectionData(String name);
	
	/**
	 * This converts {@link ConfigSection} and its data into
	 * the internal format.
	 * 
	 * @param list
	 */
	public void parseConfig(List<ConfigSection> list);
	
	/**
	 * Generates a {@link List} {@link ConfigSection} which will be saved.
	 * 
	 * @return
	 */
	public List<ConfigSection> generateConfig();
}
