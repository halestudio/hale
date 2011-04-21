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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
	 * Filter I/O provider factories by content type
	 * @param <T> the concrete I/O provider factory type
	 * 
	 * @param factories the I/O provider factories
	 * @param contentType the content type factories must support
	 * @return provider factories that support the given content type 
	 */
	public static <T extends IOProviderFactory<?>> List<T> filterFactories(
			Collection<T> factories, ContentType contentType) {
		List<T> result = new ArrayList<T>();
		
		for (T factory : factories) {
			Set<ContentType> supportedTypes = factory.getSupportedTypes();
			
			// check if contentType is supported
			for (ContentType test : supportedTypes) {
				if (isCompatibleContentType(test, contentType)) {
					result.add(factory);
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Test if the given value content type is compatible with the given 
	 * parent content type
	 * 
	 * @param parentType the parent content type
	 * @param valueType the value content type
	 * @return if the value content type is compatible with the parent content 
	 *   type
	 */
	public static boolean isCompatibleContentType(ContentType parentType,
			ContentType valueType) {
		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
		
		while (valueType != null) {
			if (parentType.equals(valueType)) {
				return true;
			}
			
			valueType = cts.getParentType(valueType);
		}
		
		return false;
	}

//	/**
//	 * Get the file extensions for the given content type
//	 * 
//	 * @param contentType the content type
//	 * @param prefix the prefix to add before the extensions, e.g. "." or "*.",
//	 *   may be <code>null</code>
//	 * @return the file extensions or <code>null</code>
//	 */
//	public static String[] getFileExtensions(ContentType contentType,
//			String prefix) {
//		SortedSet<String> exts = new TreeSet<String>();
//		
//		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
//		String[] typeExts = cts.getFileExtensions(contentType);
//		if (typeExts != null) {
//			for (String typeExt : typeExts) {
//				if (prefix == null) {
//					exts.add(typeExt);
//				}
//				else {
//					exts.add(prefix + typeExt);
//				}
//			}
//		}
//		
//		if (exts.isEmpty()) {
//			return null;
//		}
//		else {
//			return exts.toArray(new String[exts.size()]);
//		}
//	}

//	/**
//	 * Get all file extensions for the given content types
//	 * 
//	 * @param contentTypes the content types
//	 * @param prefix the prefix to add before the extensions, e.g. "." or "*.",
//	 *   may be <code>null</code>
//	 * @return the file extensions or <code>null</code>
//	 */
//	public static String[] getFileExtensions(Iterable<ContentType> contentTypes,
//			String prefix) {
//		SortedSet<String> exts = new TreeSet<String>();
//		
//		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
//		for (ContentType contentType : contentTypes) {
//			String[] typeExts = cts.getFileExtensions(contentType);
//			if (typeExts != null) {
//				for (String typeExt : typeExts) {
//					if (prefix == null) {
//						exts.add(typeExt);
//					}
//					else {
//						exts.add(prefix + typeExt);
//					}
//				}
//			}
//		}
//		
//		if (exts.isEmpty()) {
//			return null;
//		}
//		else {
//			return exts.toArray(new String[exts.size()]);
//		}
//	}

	/**
	 * Get the display name for the given content type
	 * 
	 * @param contentType the content type
	 * @return the display name
	 */
	public static String getDisplayName(ContentType contentType) {
		ContentTypeService cts = OsgiUtils.getService(ContentTypeService.class);
		return cts.getDisplayName(contentType);
	}
	
}
