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

package eu.esdihumboldt.hale.core.io.service.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.osgi.framework.Bundle;

import com.google.common.base.Preconditions;
import com.google.common.io.InputSupplier;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.extender.ContextBundleTracker;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.ContentTypeTester;
import eu.esdihumboldt.hale.core.io.internal.ContentTypeDefinition;
import eu.esdihumboldt.hale.core.io.service.ContentTypeService;
import eu.esdihumboldt.hale.core.util.DependencyOrderedList;

/**
 * Tracks {@link ContentTypeDefinition} files
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ContentTypeTracker extends ContextBundleTracker implements ContentTypeService {
	
	private static final ALogger log = ALoggerFactory.getLogger(ContentTypeTracker.class);
	
	private final Map<ContentType, BundleContentType> contentTypes = new HashMap<ContentType, BundleContentType>();

	/**
	 * Creates the content type tracker
	 */
	public ContentTypeTracker() {
		super(TrackingMode.Resolved);
	}

	/**
	 * @see ContextBundleTracker#registerBundleContextual(Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Object registerBundleContextual(Bundle bundle) {
		// load content type definitions
		Enumeration<URL> files = bundle.findEntries("META-INF/contentType", "*.xml", false);
		List<BundleContentType> types = null;
		if (files != null) {
			while (files.hasMoreElements()) {
				URL file = files.nextElement();
				
				try {
					ContentTypeDefinition def = ContentTypeDefinition.load(file.openStream());
					if (types == null) {
						types = new ArrayList<BundleContentType>();
					}
					types.add(new BundleContentType(bundle, def));
				} catch(Exception e) {
					log.error("Error loading content type definition from " + file.toString(), e);
				}
			}
		}
		
		// register content type definitions
		if (types != null) {
			synchronized (contentTypes) {
				for (BundleContentType type : types) {
					ContentType ct = ContentType.getContentType(type.getContentType().getIdentifier());
					
					BundleContentType current = contentTypes.get(ct);
					if (current == null) {
						contentTypes.put(ct, type);
						log.info("Added definition of content type " + ct + 
								" from bundle " + type.getBundle().getSymbolicName() + ".");
					}
					else {
						//TODO allow multiple content type definitions?
						log.warn("Duplicate definition of content type " + ct + 
								" found. Definition from bundle " + 
								type.getBundle().getSymbolicName() + " is ignored.");
					}
				}
			}
		}
		
		return types;
	}

	/**
	 * @see ContextBundleTracker#unregisterBundleContextual(Bundle, Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void unregisterBundleContextual(Bundle bundle, Object context) {
		List<BundleContentType> types = (List<BundleContentType>) context;

		// deregister content type definitions
		if (types != null) {
			synchronized (contentTypes) {
				for (BundleContentType type : types) {
					ContentType ct = ContentType.getContentType(type.getContentType().getIdentifier());
					
					BundleContentType current = contentTypes.get(ct);
					if (current != null) {
						if (current.getBundle().equals(bundle)) {
							contentTypes.remove(ct);
						}
					}
				}
			}
		}
	}

	/**
	 * @see ContentTypeService#getContentTypes()
	 */
	@Override
	public Iterable<ContentType> getContentTypes() {
		return Collections.unmodifiableSet(contentTypes.keySet());
	}

	/**
	 * @see ContentTypeService#getParentType(ContentType)
	 */
	@Override
	public ContentType getParentType(ContentType contentType) {
		BundleContentType type = getBundleContentType(contentType);
		if (type != null) {
			String parent =  type.getContentType().getParent();
			if (parent != null) { 
				return ContentType.getContentType(parent);
			}
		}
		
		return null;
	}

	/**
	 * @see ContentTypeService#getDisplayName(ContentType)
	 */
	@Override
	public String getDisplayName(ContentType contentType) {
		BundleContentType type = getBundleContentType(contentType);
		if (type != null) {
			Map<Locale, String> names = type.getContentType().getNames();
			if (names != null) {
				// try to get a localized name
				
				// default locale
				Locale locale = Locale.getDefault();
				String candidate = names.get(locale);
				if (candidate != null) return candidate;
				
				// default locale with language only
				locale = new Locale(locale.getLanguage());
				candidate = names.get(locale);
				if (candidate != null) return candidate;
			}
			
			// fall-back to default name
			return type.getContentType().getDefaultName();
		}
		else {
			// default display name if no definition is present
			return contentType + " file";
		}
	}

	private BundleContentType getBundleContentType(ContentType contentType) {
		BundleContentType type;
		synchronized (contentTypes) {
			type = contentTypes.get(contentType);
		}
		return type;
	}

	/**
	 * @see ContentTypeService#findContentTypesFor(Iterable, InputSupplier, String)
	 */
	@Override
	public List<ContentType> findContentTypesFor(Iterable<ContentType> types,
			InputSupplier<? extends InputStream> in, String filename) {
		Preconditions.checkArgument(filename != null || in != null, 
				"At least one of input supplier and file name must not be null");
		
		List<ContentType> results = new ArrayList<ContentType>();
		
		if (filename != null) {
			// test file extension
			for (ContentType type : types) {
				BundleContentType bct = getBundleContentType(type);
				if (bct != null) {
					String ext = FilenameUtils.getExtension(filename);
					if (ext != null && !ext.isEmpty()) {
						boolean match = bct.getContentType().getFileExtensions().contains(ext) ||
							bct.getContentType().getFileExtensions().contains("." + ext);
						if (match) {
							results.add(type);
						}
					}
				}
				else {
					log.warn("No content type definition found for ID {0}", type.getIdentifier());
				}
			}
		}
		
		if ((results.isEmpty() || results.size() > 1) && in != null) {
			// only use the testers if
			// - we have no results from the filename match
			// - we have more than one result from the filename match (as we might have to restrict the result)
			// - the input supplier is set
			
			// build a map to commit to DependencyOrderedList
			Map<ContentType, Set<ContentType>> map = new HashMap<ContentType, Set<ContentType>>();
			
			for (ContentType type : types){
				BundleContentType bct = getBundleContentType(type);
				if (bct != null){
					Set<ContentType> set = new HashSet<ContentType>();
					String father = bct.getContentType().getParent();
					if (father != null){
						set.add(ContentType.getContentType(father));
						map.put(type, set);
					}
					else {
						map.put(type, null);
					}
				}
			}
			
			// order the given content types
			DependencyOrderedList<ContentType> orderedlist = new DependencyOrderedList<ContentType>(map);
			List<ContentType> list = orderedlist.getInternalList();
			
			// last content type has to check first (has the most dependencies)
			for (int i = list.size() - 1; i >= 0; i--){
				ContentType cont = list.get(i);
				BundleContentType bct = getBundleContentType(cont);
				ContentTypeTester tester = bct.getTester();
				if (tester != null) {
					try {
						InputStream is = in.getInput();
						try {
							if (tester.matchesContentType(is)) {
								results.add(cont);
								return results;
							}

						} finally {
							try {
								is.close();
							} catch (IOException e) {
								// ignore
							}
						}
					} catch (IOException e) {
						log.warn("Could not open input stream for testing the content type, tester for content type {0} is ignored", cont);
					}
				}
			}
		}
		
		return results;
	}

	/**
	 * @see ContentTypeService#getFileExtensions(ContentType)
	 */
	@Override
	public String[] getFileExtensions(ContentType contentType) {
		BundleContentType type = getBundleContentType(contentType);
		if (type != null) {
			Set<String> extensions = type.getContentType().getFileExtensions();
			if (extensions != null) {
				String[] result = new String[extensions.size()];
				int index = 0;
				for (String ext : extensions) {
					ext = ext.trim();
					if (ext.startsWith(".")) {
						ext = ext.substring(1);
					}
					result[index++] = ext;  
				}
				
				return result;
			}
		}
		
		return null;
	}

}
