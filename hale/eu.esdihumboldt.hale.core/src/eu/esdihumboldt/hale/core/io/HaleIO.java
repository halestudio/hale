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

import java.util.SortedSet;
import java.util.TreeSet;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.core.io.service.ContentTypeService;


/**
 * Hale I/O utilities
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class HaleIO {

	/**
	 * Get all file extensions for the given content types
	 * 
	 * @param contentTypes the content types
	 * @return the file extensions or <code>null</code>
	 */
	public static String[] getFileExtensions(Iterable<ContentType> contentTypes) {
		SortedSet<String> exts = new TreeSet<String>();
		
		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
		for (ContentType contentType : contentTypes) {
			String[] typeExts = cts.getFileExtensions(contentType);
			if (typeExts != null) {
				for (String typeExt : typeExts) {
					exts.add(typeExt);
				}
			}
		}
		
		if (exts.isEmpty()) {
			return null;
		}
		else {
			return exts.toArray(new String[exts.size()]);
		}
	}
	
}
