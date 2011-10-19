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

package eu.esdihumboldt.hale.common.core.io.service;

import java.io.InputStream;
import java.util.List;

import com.google.common.io.InputSupplier;

import eu.esdihumboldt.hale.common.core.io.ContentType;

/**
 * Content type service
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
@Deprecated
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
	 * Get the parent type for a given content type
	 * 
	 * @param contentType the content type
	 * @return the parent type or <code>null</code> if there is none
	 */
	public ContentType getParentType(ContentType contentType);
	
	/**
	 * Get the file extensions for the given content type
	 * 
	 * @param contentType the content type
	 * @return the file extensions without leading dot, <code>null</code> if
	 *   none are registered for the content type
	 */
	public String[] getFileExtensions(ContentType contentType);
	
	//TODO as needed: public ContentType findContentTypeFor(InputStream in, String filename);
	
	/**
	 * Find the content types that match the given file name and/or input.
	 * 
	 * NOTE: The implementation should try to restrict the result to one 
	 * content type and only use the input supplier if absolutely needed.
	 * 
	 * @param types the types to match 
	 * @param in the input supplier to use for testing, may be <code>null</code>
	 *   if the file name is not <code>null</code>
	 * @param filename the file name, may be <code>null</code> if the input
	 *   supplier is not <code>null</code>
	 * @return the matched content types
	 */
	public List<ContentType> findContentTypesFor(Iterable<ContentType> types, 
			InputSupplier<? extends InputStream> in, String filename);

}
