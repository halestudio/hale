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

package eu.esdihumboldt.hale.core.io.service;

import eu.esdihumboldt.hale.core.io.ContentType;

/**
 * Content type service
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface ContentTypeService {
	
	/**
	 * Get the registered content types
	 * 
	 * @return the registered content types
	 */
	public Iterable<ContentType> getContentTypes();
	
	/**
	 * Get the display name for the given content type
	 * 
	 * @param contentType the content type
	 * @return the display name
	 */
	public String getDisplayName(ContentType contentType);
	
	/**
	 * Get the file extensions for the given content type
	 * 
	 * @param contentType the content type
	 * @return the file extensions without leading dot, <code>null</code> if
	 *   none are registered for the content type
	 */
	public String[] getFileExtensions(ContentType contentType);
	
	//TODO as needed: public ContentType findContentTypeFor(InputStream in, String filename);
	//TODO as needed: public ContentType findContentTypeFor(Iterable<ContentType> types, InputStream in, String filename);

}
