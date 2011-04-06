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

package eu.esdihumboldt.hale.models.config;

/**
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ConfigSchemaServiceListener {
	
	/**
	 * 
	 * @param section the section
	 * @param message one of the {@link ConfigSchemaServiceListener#Message}
	 */
	public void update(String section, Message message);
	
	/**
	 * Contains the status messages for {@link ConfigSchemaServiceListener} updates
	 * 
	 * @author Andreas Burchert
	 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
	 * @version $Id$
	 */
	public enum Message { //TODO please comment the entries
		ITEM_ADDED, 
		ITEM_REMOVED, 
		ITEM_CHANGED,
		SECTION_ADDED, 
		SECTION_REMOVED,
		CONFIG_PARSED, 
		CONFIG_GENERATED
	}
}
