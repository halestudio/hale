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

package eu.esdihumboldt.hale.core.io;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a content type.<br />
 * <br />
 * NOTE: the content type mechanism of Eclipse is not used at this point, as
 * the I/O poviders must be usable also in a context outside of Eclipse. 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public final class ContentType {
	
	private static final Map<String, ContentType> types = new HashMap<String, ContentType>();
	
	/**
	 * Get the content type with the given identifier
	 * 
	 * @param identifier the identifier
	 * 
	 * @return the content-type
	 */
	public static ContentType getContentType(String identifier) {
		ContentType type = types.get(identifier);
		if (type == null) {
			type = new ContentType(identifier);
			types.put(identifier, type);
		}
		return type;
	}

	private final String identifer;
	
	/**
	 * Create a content type with the given identifier
	 * 
	 * @param identifier the identifier
	 */
	private ContentType(String identifier) {
		super();
		
		this.identifer = identifier;
	}

	/**
	 * Get the content type identifier
	 * 
	 * @return the content type identifier
	 */
	public String getIdentifier() {
		return identifer;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return getIdentifier();
	}
	
}
